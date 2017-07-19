/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univnantes.fedOrNot.spark;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import scala.Tuple2;
import weka.classifiers.trees.RandomForest;

/**
 * http://spark.apache.org/docs/latest/mllib-decision-tree.html
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class TrainFromArff_DecisionTree {

    private static final Logger logger = Logger.getLogger(TrainFromArff_DecisionTree.class);

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setAppName("SPARQL-logs-analytics")
                .setMaster("local[2]");
//                .setMaster("spark://node001:7077");
//                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> data = sc.textFile("/Users/gaignard-a/Desktop/Learning-apache-spark/data/distinct_federated_vs_single_WITHOUT_STRING_QUERY.arff");

        JavaRDD<LabeledPoint> single
                = data.filter(line -> (!line.startsWith("@") && (line.length() > 1)))
                .map(line -> line.split(","))
                .filter(fields -> {
                    return fields[16].contains("single");
                })
                .map(new Function<String[], LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String[] arg0) throws Exception {
                        double[] d = new double[arg0.length - 1];
                        for (int i = 0; i < arg0.length - 1; i++) {
                            d[i] = Double.parseDouble(arg0[i]);
                        }
                        return new LabeledPoint(0, Vectors.dense(d));
                    }
                })
                //                .take(10).forEach(p -> logger.info(p));
                .cache();

        JavaRDD<LabeledPoint> fed
                = data.filter(line -> (!line.startsWith("@") && (line.length() > 1)))
                .map(line -> line.split(","))
                .filter(fields -> {
                    return fields[16].contains("fed");
                })
                .map(new Function<String[], LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String[] arg0) throws Exception {
                        double[] d = new double[arg0.length - 1];
                        for (int i = 0; i < arg0.length - 1; i++) {
                            d[i] = Double.parseDouble(arg0[i]);
                        }
                        return new LabeledPoint(1, Vectors.dense(d));
                    }
                })
                //                .take(10).forEach(p -> logger.info(p));
                .cache();

//        single.union(fed).take(100).forEach(p -> logger.info(p)); 
        JavaRDD<LabeledPoint> allFeatures = single.union(fed);
//        logger.info(allFeatures.count()+  " features " );

        // Split the data into training and test sets (30% held out for testing)
        JavaRDD<LabeledPoint>[] splits = allFeatures.randomSplit(new double[]{0.7, 0.3});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        // Set parameters.
        //  Empty categoricalFeaturesInfo indicates all features are continuous.
        Integer numClasses = 2;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<Integer, Integer>();
        categoricalFeaturesInfo.put(0, 2);
        categoricalFeaturesInfo.put(1, 2);
        categoricalFeaturesInfo.put(2, 2);
        categoricalFeaturesInfo.put(5, 2);
        categoricalFeaturesInfo.put(6, 2);
        //features 0, 1, 2, 5, 6 are binary (boolean variables)
        String impurity = "gini";
        Integer maxDepth = 5;
        Integer maxBins = 32;

// Train a DecisionTree model for classification.
        final DecisionTreeModel model = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo, impurity, maxDepth, maxBins);

// Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel
                = testData.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
                    @Override
                    public Tuple2<Double, Double> call(LabeledPoint p) {
                        return new Tuple2<Double, Double>(model.predict(p.features()), p.label());
                    }
                });
        Double testErr
                = 1.0 * predictionAndLabel.filter(new Function<Tuple2<Double, Double>, Boolean>() {
                    @Override
                    public Boolean call(Tuple2<Double, Double> pl) {
                        return !pl._1().equals(pl._2());
                    }
                }).count() / testData.count();

        System.out.println("Test Error: " + testErr);
        System.out.println("Learned classification tree model:\n" + model.toDebugString());

// Save and load model
//        model.save(sc.sc(), "target/tmp/myDecisionTreeClassificationModel");
//        DecisionTreeModel sameModel = DecisionTreeModel
//                .load(sc.sc(), "target/tmp/myDecisionTreeClassificationModel");
    }
}
