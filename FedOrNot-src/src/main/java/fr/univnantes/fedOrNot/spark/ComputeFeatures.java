/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univnantes.fedOrNot.spark;

import fr.univnantes.fedOrNot.parser.SPARQLVisitor;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class ComputeFeatures implements Function<String, LabeledPoint> {

    private static final Logger logger = Logger.getLogger(ComputeFeatures.class);
    private static final long serialVersionUID = 24L;

    public LabeledPoint call(String s) {
        String queryS = s;
        Query q = null;
        try {
            String commonPrefix = "PREFIX dbpedia: <http://dbpedia.org/resource/> "
                    + "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
                    + "PREFIX dbpprop: <http://dbpedia.org/property/> "
                    + "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
                    + "PREFIX dcterms: <http://purl.org/dc/terms/> "
                    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                    + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                    + "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
                    + "PREFIX geonames: <http://www.geonames.org/ontology#> "
                    + "PREFIX geodata: <http://sws.geonames.org/> "
                    + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
                    + "PREFIX bif: <bif:> "
                    + "PREFIX dbo: <http://dbpedia.org/ontology/> "
                    + "PREFIX dbp: <http://dbpedia.org/property/> "
                    + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                    + "PREFIX dct: <http://purl.org/dc/terms/> "
                    + "PREFIX yago: <http://dbpedia.org/class/yago/> "; // bif dbo skos dct
            queryS = commonPrefix + queryS;
            q = QueryFactory.create(queryS);
        } catch (QueryParseException e) {
//            logger.error("Impossible to parse, skipping query: " + queryS);
//            logger.error(e.getMessage());
            return new LabeledPoint(-1.0d, Vectors.dense(-1.0d));
        } catch (QueryException e) {
//            logger.error("Impossible to parse, skipping query: " + queryS);
//            logger.error(e.getMessage());
            return new LabeledPoint(-1.0d, Vectors.dense(-1.0d));
        } catch (Exception e) {
//            logger.error("Unknown exceptionOlivier Caron, skipping query: " + queryS);
            return new LabeledPoint(-1.0d, Vectors.dense(-1.0d));
        }
        

        double[] featureValues = new double[16];
        //passing to the visitor all features already extracted from the query
        SPARQLVisitor v = new SPARQLVisitor(q.isAskType(),
                q.isSelectType(),
                q.isConstructType(),
                q.hasLimit() ? q.getLimit() : -1,
                q.hasOffset() ? q.getOffset() : -1,
                (q.getPrefixMapping().getNsPrefixMap().size() > 0)
        );
        Op op = Algebra.compile(q);
        OpWalker.walk(op, v); // call the walker on the visitor
        // copy every visitors features values on the double vector
        featureValues[0] = v.isASK() ? 1 : 0;
        featureValues[1] = v.isSELECT() ? 1 : 0;
        featureValues[2] = v.isCONSTRUCT() ? 1 : 0;
        featureValues[3] = v.getValOFFSET();
        featureValues[4] = v.getValLIMIT();
        featureValues[5] = v.getNbVarIncrements();
        featureValues[6] = v.usePrefix() ? 1 : 0;
        featureValues[7] = v.getNbGROUPBY();
        featureValues[8] = v.getNbUNION();
        featureValues[9] = v.getNbFILTER();
        featureValues[10] = v.getNbORDERBY();
        featureValues[11] = v.getNbBGP();
        featureValues[12] = v.getNbTRIPLE();
        featureValues[13] = v.getNbSERVICE();
        featureValues[14] = v.getLevenshtein();
        featureValues[15] = v.getNbOperator();
//                featureValues[16] = attVals.indexOf(classLabel);
//                featureValues[17] = data.attribute("Query").addStringValue(Utils.backQuoteChars(query));
//        vals[17] = data.attribute("Query").addStringValue("test");

        return new LabeledPoint(0, Vectors.dense(featureValues));
    }
}
