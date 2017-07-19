/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import fr.univnantes.fedOrNot.parser.DataInstanceV2;
import fr.univnantes.fedOrNot.parser.DataInstanceV3_noask;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.Tuple2;
import weka.classifiers.Classifier;
import weka.core.Instance;

/**
 *
 * @author Alban Gaignard <alban.gaignard@univ-nantes.fr>
 */
public class SPARK_Predictions2csv_Test {

    public static Logger logger = Logger.getLogger(SPARK_PredictionTest.class);

    public enum Headers {
        Filename, Single, Federated
    }

    public SPARK_Predictions2csv_Test() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() throws IOException {

        //Weka classifier
        //load model
        String inputLogsDir = "/Users/gaignard-a/Desktop/inExpe";
        File[] inputLogs = (new File(inputLogsDir)).listFiles();

        File tmp = File.createTempFile("spark-output-", ".csv");
        File tmpPred = File.createTempFile("spark-predicted-single", ".log");
        System.out.println("Writing output CSV to " + tmp.getAbsolutePath());
        System.out.println("Writing predictions to " + tmpPred.getAbsolutePath());
        
        CSVPrinter csvFilePrinter = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader("Filename", "Single", "Federated");
        FileWriter fileWriter = new FileWriter(tmp);
        FileWriter predFileWriter = new FileWriter(tmpPred);
        csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

        String rootPath = "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/experiments/training_data/";
        try {
            final Classifier cls = (Classifier) weka.core.SerializationHelper.read(rootPath + "ground_truth_50_50.model");
            System.out.println(cls);

            StopWatch sw = new StopWatch();
            sw.start();

            SparkConf conf = new SparkConf()
                    .setAppName("SPARQL-logs-analytics")
                    .setMaster("local[4]");
//                .setMaster("spark://node001:7077");
//                .setMaster("local");
            JavaSparkContext sc = new JavaSparkContext(conf);

            for (File f : inputLogs) {
                String path = f.getAbsolutePath();

                if (path.contains("access")) {

                    JavaRDD<String> data = sc.textFile(f.getAbsolutePath());
                    data = data.repartition(8);
                    System.out.println(data.getNumPartitions());
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
                            //                .filter(new FilterPredictedFedQuery(cls))
                            .cache();
                    logger.info("SPARK query retrieving done in " + sw.getTime() + " ms.");
//                    System.out.println(queries.count());

                    JavaPairRDD<String, Iterable<String>> queryPredictions
                            = queries.mapToPair(query -> {
                                List<String> labels = Arrays.asList("?", "single", "fed");
                                DataInstanceV3_noask instances = new DataInstanceV3_noask("testData", labels);

                                instances.addData(query, "?");
                                try {
                                    Instance instance = instances.getInstance(0);
                                    double value = cls.classifyInstance(instance);
                                    String predLabel = instances.getData().classAttribute().value((int) value);
                                    if (predLabel.contains("fed")) {
//                                        predFileWriter.write(instance.toString()+"\n");
//                                        System.out.println(instance.toString());
                                    }
                                    return new Tuple2(predLabel, query);

                                } catch (Exception ex) {
                                    return new Tuple2("unclassified", query);
                                }
                            }).cache();

                    logger.info("SPARK query prediction done in " + sw.getTime() + " ms.");
                    
                    
                    Object countSingleObj = queryPredictions.countByKey().get("single");
                    Object countFedObj = queryPredictions.countByKey().get("fed");
                    long countSingle = 0;
                    long countFed = 0;
                    if (countSingleObj != null) {
                        countSingle = (long) countSingleObj;
                    }
                    if (countFedObj != null) {
                        countFed = (long) countFedObj;
                    }

                    logger.info("Predicted " + countSingle + " single queries from log (" + sw.getTime() + " ms)");
                    logger.info("Predicted " + countFed + " federated queries from log (" + sw.getTime() + " ms)");
                    logger.info("SPARK process done in " + sw.getTime() + " ms.");

                    csvFilePrinter.printRecord("access.log-20150818_test_lite.txt", countSingle, countFed);
                    
                    queryPredictions.filter(i -> i._1().contains("single"))
                            .take(10)
                            .forEach(t -> {
                                System.out.println(t._1);
                                System.out.println(t._2);
                                System.out.println("----");
                            });
                }
            }
            sw.stop();
            
            predFileWriter.flush();
            predFileWriter.close();
            fileWriter.flush();
            fileWriter.close();
            csvFilePrinter.close();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SPARK_PredictionTest.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        logger.info("CSV written to " + tmp. getAbsolutePath());
    }
}
