/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import fr.univnantes.fedOrNot.spark.FilterPredictedFedQuery;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;
import org.apache.jena.ext.com.google.common.base.Splitter;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import weka.classifiers.Classifier;

/**
 *
 * @author Alban Gaignard <alban.gaignard@univ-nantes.fr>
 */
public class SPARK_PredictionTest {

    public static Logger logger = Logger.getLogger(SPARK_PredictionTest.class);

    public SPARK_PredictionTest() {
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
        String rootPath = "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/experiments/result-model/";
        Classifier cls = null;
        try {
            cls = (Classifier) weka.core.SerializationHelper.read(rootPath + "ground_truth_50_50.model");
            System.out.println(cls.debugTipText());
            System.out.println(cls);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SPARK_PredictionTest.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }

        SparkConf conf = new SparkConf()
                .setAppName("SPARQL-logs-analytics")
                .setMaster("local[*]");
//                .setMaster("spark://node001:7077");
//                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> data = sc.textFile("src/main/resources/Logs/access.log-20150818_test_lite.txt");
        data = data.repartition(8);
        System.out.println(data.getNumPartitions());
//        JavaRDD<String> data = sc.textFile("/Users/gaignard-a/Desktop/access.log-20150818.bz2");

        JavaRDD<String> predictedFederatedQueries = data.filter(line -> line.toLowerCase().contains("/sparql"))
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
                .filter(new FilterPredictedFedQuery(cls))
                .cache();

        long nbFed = predictedFederatedQueries.count();
        logger.info("Predicted " + nbFed + " federated queries from log");

//        predictedFederatedQueries.take(10).forEach(vect -> logger.info(vect));
    }
}
