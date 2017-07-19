/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univnantes.fedOrNot.spark;

import fr.univnantes.fedOrNot.parser.DataInstanceV2;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import static org.netlib.arpack.Dnaitr.j;
import weka.classifiers.Classifier;
import weka.core.Instance;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class FilterPredictedFedQuery implements Function<String, Boolean> {

    private static final Logger logger = Logger.getLogger(FilterPredictedFedQuery.class);
    private static final long serialVersionUID = 24L;
    private Classifier classifier = null;

    public Classifier getClassifier() {
        return classifier;
    }
    
    public FilterPredictedFedQuery(Classifier cls) {
        classifier = cls;
    }

    public Boolean call(String s) throws Exception {

        String query = s;

        List<String> labels = Arrays.asList("?", "single", "fed");
        DataInstanceV2 instances = new DataInstanceV2("testData", labels);
        
        instances.addData(query, "?");
        try {
            Instance instance = instances.getInstance(0);
            double value = getClassifier().classifyInstance(instance);
            String predLabel = instances.getData().classAttribute().value((int) value);
//            if (predLabel.equals("fed")) {
//                logger.info("FED prediction for : "+query);
//            }
            return predLabel.equals("fed");
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
}