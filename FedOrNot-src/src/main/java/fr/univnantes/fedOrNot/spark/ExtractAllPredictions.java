package fr.univnantes.fedOrNot.spark;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import fr.univnantes.fedOrNot.parser.DataInstanceV3_noask;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.time.StopWatch;
import org.apache.jena.ext.com.google.common.base.Splitter;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import weka.classifiers.Classifier;
import weka.core.Instance;

/**
 *
 * @author Alban Gaignard <alban.gaignard@univ-nantes.fr>
 */
public class ExtractAllPredictions {

    public static Logger logger = Logger.getLogger(ExtractAllPredictions.class);

    public enum Headers {
        Filename, Single, Federated
    }

    public ExtractAllPredictions() {
    }

    public static void main(String args[]) throws IOException {

        if (args[0] == null) {
            System.out.println("please set in parameter the number of cores for the experiment");
            System.exit(0);
        }

        int nbCores = Integer.parseInt(args[0]);

//        String rootPath = "/sandbox/users/gaignard-a/dev/experiments/sparql-ml-validation";
        String inputLogsDir = args[1];
        File[] inputLogs = (new File(inputLogsDir)).listFiles();

        File tmpPred = File.createTempFile("spark-predicted-fed-", ".log", new File(inputLogsDir));
        System.out.println("Writing predictions to " + tmpPred.getAbsolutePath());
        FileWriter predFileWriter = new FileWriter(tmpPred);

        try {
            InputStream is = ExtractAllPredictions.class.getClassLoader().getResourceAsStream("random_forest.model");
            final Classifier cls = (Classifier) weka.core.SerializationHelper.read(is);
            System.out.println(cls);

            StopWatch sw = new StopWatch();
            sw.start();

            SparkConf conf = new SparkConf()
                    .setAppName("SPARQL-logs-analytics")
                    //                    .set("spark.default.parallelism", String.valueOf(nbCores))
                    .set("spark.executor.cores", String.valueOf(nbCores))
                    .setMaster("local[" + nbCores + "]");
//                .setMaster("spark://node001:7077");
//                .setMaster("local");
            JavaSparkContext sc = new JavaSparkContext(conf);

            for (File f : inputLogs) {
                String path = f.getAbsolutePath();

                if (path.contains(".log")) {
                    JavaRDD<String> data = sc.textFile(f.getAbsolutePath());
                    data = data.repartition(nbCores).cache();
//                    data = data.coalesce(nbCores, true);
                    System.out.println(nbCores + " cores and " + data.getNumPartitions() + " data partitions");
                    System.out.println(data.toDebugString());
//                JavaRDD<String> data = sc.textFile("src/main/resources/Logs/access.log-20150818_test_lite.txt");
//        JavaRDD<String> data = sc.textFile("/Users/gaignard-a/Desktop/access.log-20150818.bz2");

                    JavaRDD<String> queries = data.filter(line -> line.toLowerCase().contains("/sparql"))
                            .map(line -> line.split("\\?"))
                            .filter(fields -> {
                                return (fields.length > 1);
                            })
                            .map(fields -> fields[1])
                            .filter(query -> {
                                try {
                                    return Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query).containsKey("query");
                                } catch (IllegalArgumentException e) {
                                    return false;
                                }
                            })
                            .map(query -> {
                                return Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query).get("query");
                            })
                            .filter(query -> query.split(" ")[0] != null)
                            .map(query -> query.split(" ")[0])
                            .map(query -> {
                                try {
                                    return URLDecoder.decode(query, "UTF-8");
                                } catch (IllegalArgumentException e) {
                                    return "null";
                                }
                            })
                            .filter(query -> !query.contains("define sql:"))
                            .repartition(nbCores).cache();

                    logger.info("SPARK query retrieving done in " + sw.getTime() + " ms.");

                    JavaPairRDD<String, Iterable<String>> queryPredictions = queries.mapToPair(query -> {
                        List<String> labels = Arrays.asList("?", "single", "fed");
                        DataInstanceV3_noask instances = new DataInstanceV3_noask("testData", labels);

                        instances.addData(query, "?");
                        try {
                            Instance instance = instances.getInstance(0);
                            double value = cls.classifyInstance(instance);
                            String predLabel = instances.getData().classAttribute().value((int) value);
                            return new Tuple2(predLabel, instance.toString());
                        } catch (Exception ex) {
                            return new Tuple2("unclassified", query);
                        }
                    }).repartition(nbCores).cache();

                    queryPredictions.filter(i -> i._1().contains("fed"))
                            .collect()
                            .forEach(t -> {
                                try {
                                    predFileWriter.write(t._2 + "\n");
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                }
            }
            sw.stop();
//            System.out.println("Predictions written to " + tmpPred.getAbsolutePath());

        } catch (Exception ex) {
            logger.error(ex);

            System.exit(0);
        }
    }
}
