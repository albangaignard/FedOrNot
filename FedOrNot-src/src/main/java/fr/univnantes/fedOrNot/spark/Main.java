/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univnantes.fedOrNot.spark;

import fr.univnantes.fedOrNot.parser.DataInstanceV2;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("First parameter : spark master URL, or <local[2]> for 2-cores local execution");
            System.out.println("Second parameter : weka serialized classification model");
            System.out.println("Third parameter : Directory of log files to be classified");
            System.exit(0);
        }

        //Weka classifier
        //load model
        String inputLogsDir = args[2];
        File[] inputLogs = (new File(inputLogsDir)).listFiles();

        File tmp = File.createTempFile("spark-output-", ".csv");
        logger.info("Writing output CSV to " + tmp.getAbsolutePath());
        System.out.println("Writing output CSV to " + tmp.getAbsolutePath());
        CSVPrinter csvFilePrinter = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader("Filename", "Single", "Federated");
        FileWriter fileWriter = new FileWriter(tmp);
        csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

        try {

            final Classifier cls = (Classifier) weka.core.SerializationHelper.read(args[1]);
            System.out.println(cls.debugTipText());
            System.out.println(cls);

            StopWatch sw = new StopWatch();
            sw.start();

            SparkConf conf = new SparkConf()
                    .setAppName("SPARQL-logs-analytics")
//                    .setMaster("local[*]");
                    .setMaster(args[0]);
//                .setMaster("spark://node001:7077");
//                .setMaster("local");
            JavaSparkContext sc = new JavaSparkContext(conf);

            for (File f : inputLogs) {
                String path = f.getAbsolutePath();

                if (path.contains("access")) {
                    logger.info("Processing file " + f.getName());

                    JavaRDD<String> data = sc.textFile(f.getAbsolutePath());
//                    data = data.repartition(60);
                    data = data.coalesce(60);
//                JavaRDD<String> data = sc.textFile("src/main/resources/Logs/access.log-20150818_test_lite.txt");
//        JavaRDD<String> data = sc.textFile("/Users/gaignard-a/Desktop/access.log-20150818.bz2");

                    JavaRDD<String> queries = data.filter(line -> line.toLowerCase().contains("/sparql"))
                            .map(line -> line.split("\\?"))
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
                            .repartition(60)
                            .cache();
                    logger.info("SPARK query retrieving done in " + sw.getTime() + " ms.");
                    long totalQueries = queries.count() ;
                    logger.info(totalQueries + " parsed queries in " + sw.getTime() + " ms.");

                    JavaPairRDD<String, Iterable<String>> queryPredictions
                            = queries.mapToPair(query -> {
                                List<String> labels = Arrays.asList("?", "single", "fed");
                                DataInstanceV2 instances = new DataInstanceV2("testData", labels);

                                instances.addData(query, "?");
                                try {
                                    Instance instance = instances.getInstance(0);
                                    double value = cls.classifyInstance(instance);
                                    String predLabel = instances.getData().classAttribute().value((int) value);
                                    return new Tuple2(predLabel, query);

                                } catch (Exception ex) {
                                    return new Tuple2("unclassified", query);
                                }
                            }).cache();
                    
                    Object nbSingle = queryPredictions.countByKey().get("single");
                    logger.info("Predicted " + nbSingle + " single queries from log (" + sw.getTime() + " ms)");
                    Object nbFed = queryPredictions.countByKey().get("fed");
                    logger.info("Predicted " + nbFed + " federated queries from log (" + sw.getTime() + " ms)");
                    logger.info("SPARK process done in " + sw.getTime() + " ms.");

                    csvFilePrinter.printRecord(f.getName(), nbSingle, nbFed);
                }
            }
            sw.stop();

            fileWriter.flush();
            fileWriter.close();
            csvFilePrinter.close();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }
}
