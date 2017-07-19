/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univnantes.fedOrNot.spark;

import fr.univnantes.fedOrNot.parser.SPARQLVisitor;
import java.net.URLDecoder;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.time.StopWatch;
import org.apache.jena.ext.com.google.common.base.Splitter;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import weka.classifiers.Classifier;
import weka.core.Utils;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class Main_test {

    private static final Logger logger = Logger.getLogger(Main_test.class);

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("First parameter : spark master URL, or <local[2]> for 2-cores local execution");
            System.out.println("Second parameter : weka serialized classification model");
            System.out.println("Third parameter : log file to be classified");
            System.exit(0);
        }

        //Weka classifier
        //load model
        Classifier cls = null;
        try {
            cls = (Classifier) weka.core.SerializationHelper.read(args[1]);
            System.out.println(cls.debugTipText());
            System.out.println(cls);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        
        StopWatch sw = new StopWatch();
        sw.start();

        SparkConf conf = new SparkConf()
                .setAppName("SPARQL-logs-analytics")
//                                .setMaster("local[2]");
                //                .setMaster("spark://node001:7077");
                //                .setMaster("local");
                .setJars(new String[]{"/sandbox/agaignard/dev/experiments/sparql-ml-validation/GenSparqlFeatures-0.4-SNAPSHOT-launcher.jar"})
                .setMaster(args[0]);

        JavaSparkContext sc = new JavaSparkContext(conf);

//        JavaRDD<String> data = sc.textFile("/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/Usewod20016/usewod2016-access.log-20150818_ONLY_SELECT_1K_lines.txt");
        JavaRDD<String> data = sc.textFile(args[2]);

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
        sw.stop();
        logger.info("Predicted " + nbFed + " federated queries from log");
        logger.info("SPARK process done in "+sw.getTime()+ " ms.");

    }
}
