/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import fr.univnantes.fedOrNot.parser.DataInstanceV2;
import fr.univnantes.fedOrNot.parser.DataInstanceV3_noask;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;

/**
 *
 * @author Alban Gaignard <alban.gaignard@univ-nantes.fr>
 */
public class WekaPredictTest {

    public WekaPredictTest() {
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
    public void hello() throws Exception {
        //load model
//        String rootPath = "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/experiments/result-model/";
        String rootPath = "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/experiments/model/";
        Classifier cls = (Classifier) weka.core.SerializationHelper.read(rootPath + "ground_truth_50_50.model");
        System.out.println(cls.debugTipText());
        System.out.println(cls);

        String q1 = ""
                + "SELECT ?x_1 ?x_2 WHERE {"
                + "     ?x_1 foaf:name ?x_2"
                + "}";

        String q2 = ""
                + "SELECT ?available ?another WHERE {"
                + "     ?avariable foaf:name ?another ."
                + "     ?avariable foaf:name1 ?another ."
                + "     ?avariable foaf:name2 ?another ."
                + "     ?avariable foaf:name3 ?another ."
                + "}";

        String q3 = "SELECT  ?o_5 ?o_4 ?o_7 ?o_6 ?o_1 ?o_10 ?o_0 ?o_3 ?o_2 ?o_14 ?o_13 ?o_12 ?o_11 ?o_9 ?o_8 WHERE { "
                + "{ <http://dbpedia.org/resource/Robert_F._Kennedy> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_0 FILTER (?o_0 != <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Maria_Shriver> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_1 FILTER (?o_1 != <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Malcolm_Smith_%28U.S._politician%29> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_2 FILTER (?o_2 != <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Phil_Gramm> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_3 FILTER (?o_3 != <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Harvey_Milk> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_4 FILTER (?o_4 != <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Charles_W._Bryan> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_5 FILTER (?o_5 != <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Henry_A._Wallace> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_6 FILTER (?o_6 != <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Larry_Agran> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_7 FILTER (?o_7 != <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/John_W._Davis> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_8 FILTER (?o_8 = <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Hattie_Caraway> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_9 FILTER (?o_9 = <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Gaylord_Nelson> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_10 FILTER (?o_10 = <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Alan_Colmes> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_11 FILTER (?o_11 = <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Mary_Jo_Kopechne> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_12 FILTER (?o_12 = <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/J._William_Fulbright> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_13 FILTER (?o_13 = <http://dbpedia.org/ontology/President> ) } "
                + "UNION { <http://dbpedia.org/resource/Martin_Ch%C3%A1vez> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_14 FILTER (?o_14 = <http://dbpedia.org/ontology/President> ) } }";

        String q4 = "SELECT ?abstract WHERE { "
                + "<http://dbpedia.org/resource/Bridgeton_High_School> <http://dbpedia.org/ontology/abstract> ?abstract. "
                + "FILTER langMatches(lang(?abstract), 'en') }";

        String q5 = "SELECT ?abstract ?comment WHERE { "
                + "<http://dbpedia.org/resource/Bridgeton_High_School> <http://dbpedia.org/ontology/abstract> ?abstract. "
                + "<http://dbpedia.org/resource/Bridgeton_High_School> rdfs:comment ?comment. "
                + "}";

        String q6 = "SELECT  ?o_5 ?o_4 ?o_7 ?o_6 ?o_1 ?o_10 ?o_0 ?o_3 ?o_2 ?o_14 ?o_13 ?o_12 ?o_11 ?o_9 ?o_8 WHERE { "
                + " <http://dbpedia.org/resource/Robert_F._Kennedy> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_0 "
                + "FILTER ((?o_0 = <http://dbpedia.org/ontology/President>) ||"
                + "(?o_0 = <http://dbpedia.org/ontology/Pres>) ||"
                + " (?o_0 = <http://dbpedia.org/ontology/P>) ) . "
                + "OPTIONAL { <http://dbpedia.org/resource/Maria_Shriver> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o_1 FILTER (?o_1 = <http://dbpedia.org/ontology/President> ) } "
                + "} ";

        String qAnapsid = "PREFIX dbpedia: <http://dbpedia.org/resource/> "
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
                + "PREFIX bif: <bif:> PREFIX dbo: <http://dbpedia.org/ontology/> "
                + "PREFIX dbp: <http://dbpedia.org/property/> "
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + "PREFIX dct: <http://purl.org/dc/terms/> "
                + "PREFIX yago: <http://dbpedia.org/class/yago/> "
                + "SELECT ?president ?x ?page WHERE {        "
                + "?x <http://data.nytimes.com/elements/topicPage> ?page  . "
                + "?x <http://www.w3.org/2002/07/owl#sameAs> ?president . "
                + "FILTER ((?president=<http://dbpedia.org/resource/Evan_Bayh>) || (?president=<http://dbpedia.org/resource/John_McCain>) || (?president=<http://dbpedia.org/resource/Teller_(magician)>) || (?president=<http://dbpedia.org/resource/Barack_Obama>) || (?president=<http://dbpedia.org/resource/James_Dobson>) || (?president=<http://dbpedia.org/resource/Al_Franken>) || (?president=<http://dbpedia.org/resource/Joe_Lieberman>) || (?president=<http://dbpedia.org/resource/Lloyd_Blankfein>) || (?president=<http://dbpedia.org/resource/Herman_Badillo>) || (?president=<http://dbpedia.org/resource/Conrad_Burns>))} LIMIT 10000 OFFSET 0";

        String qFedx = "PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> PREFIX dbpprop: <http://dbpedia.org/property/> PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX dcterms: <http://purl.org/dc/terms/> PREFIX foaf: <http://xmlns.com/foaf/0.1/>PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> PREFIX geonames: <http://www.geonames.org/ontology#> PREFIX geodata: <http://sws.geonames.org/> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX bif: <bif:> PREFIX dbo: <http://dbpedia.org/ontology/> PREFIX dbp: <http://dbpedia.org/property/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#> PREFIX dct: <http://purl.org/dc/terms/> PREFIX yago: <http://dbpedia.org/class/yago/> \n"
                + "SELECT  ?o_5 ?o_4 ?o_7 ?o_6 ?o_1 ?o_10 ?o_0 ?o_3 ?o_2 ?o_14 ?o_13 ?o_12 ?o_11 ?o_9 ?o_8 WHERE \n"
                + "{ { <http://bio2rdf.org/dr:D01614> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_0 FILTER (?o_0 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01617> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_1 FILTER (?o_1 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01618> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_2 FILTER (?o_2 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01621> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_3 FILTER (?o_3 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01622> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_4 FILTER (?o_4 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01623> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_5 FILTER (?o_5 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01630> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_6 FILTER (?o_6 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01631> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_7 FILTER (?o_7 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01635> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_8 FILTER (?o_8 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01636> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_9 FILTER (?o_9 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01647> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_10 FILTER (?o_10 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01653> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_11 FILTER (?o_11 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01657> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_12 FILTER (?o_12 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01661> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_13 FILTER (?o_13 = <http://bio2rdf.org/cas:58-27-5> ) } \n"
                + "UNION { <http://bio2rdf.org/dr:D01666> <http://bio2rdf.org/ns/bio2rdf#xRef> ?o_14 FILTER (?o_14 = <http://bio2rdf.org/cas:58-27-5> ) } }";

        String qDistinct = "SELECT DISTINCT  ?t WHERE { \n"
                + "     ?s rdf:type <http://dbpedia.org/class/yago/PeopleFromStPancras,London> . \n"
                + "     ?s <http://dbpedia.org/ontology/team> ?o . \n"
                + "     ?o rdf:type ?t } \n"
                + "OFFSET  0 \n"
                + "LIMIT   10000 \n";
        
        String qSTAR = "SELECT * WHERE { \n"
                + "     ?s rdf:type <http://dbpedia.org/class/yago/PeopleFromStPancras,London> . \n"
                + "     ?s <http://dbpedia.org/ontology/team> ?o . \n"
                + "     ?o rdf:type ?t } \n"
                + "OFFSET  0 \n"
                + "LIMIT   10000 \n";
        
        String qDistinctSTAR = "SELECT DISTINCT * WHERE { \n"
                + "     ?s rdf:type <http://dbpedia.org/class/yago/PeopleFromStPancras,London> . \n"
                + "     ?s <http://dbpedia.org/ontology/team> ?o . \n"
                + "     ?o rdf:type ?t } \n"
                + "OFFSET  0 \n"
                + "LIMIT   10000 \n";
                
        // same order as when building the model !! 
        List<String> labels = Arrays.asList("?", "single", "fed");
        DataInstanceV3_noask instances = new DataInstanceV3_noask("testData", labels);
//        instances.addData(q1, "?");
//        instances.addData(q2, "?");
//        instances.addData(q3, "?");
//        instances.addData(q4, "?");
//        instances.addData(q5, "?");
//        instances.addData(q6, "?");
//        instances.addData(qAnapsid, "?");
//        instances.addData(qFedx, "?");
        instances.addData(qDistinct, "?");
        instances.addData(qSTAR, "?");
        instances.addData(qDistinctSTAR, "?");



//        atts.addElement(new Attribute("isSELECT"));
//        atts.addElement(new Attribute("isCONSTRUCT"));
//        atts.addElement(new Attribute("valOFFSET"));
//        atts.addElement(new Attribute("valLIMIT"));
//        atts.addElement(new Attribute("nbVarIncrements"));
//        atts.addElement(new Attribute("hasPREFIX"));
//        atts.addElement(new Attribute("nbGROUPBY"));
//        atts.addElement(new Attribute("nbUNION"));
//        atts.addElement(new Attribute("nbFILTER"));
//        atts.addElement(new Attribute("nbORDERBY"));
//        atts.addElement(new Attribute("nbBGP"));
//        atts.addElement(new Attribute("nbTRIPLE"));
//        atts.addElement(new Attribute("nbOPTIONAL"));
//        atts.addElement(new Attribute("levenshtein"));
//        atts.addElement(new Attribute("nbORInFILTER"));
//        atts.addElement(new Attribute("nbNotEqualsInFILTER"));
//        atts.addElement(new Attribute("nbEqualsInFILTER"));
//        atts.addElement(new Attribute("nbOperator"));

        for (int j = 0; j < instances.getData().numInstances(); j++) {
            Instance instance = instances.getInstance(j);
            System.out.println(instance);

            double value = cls.classifyInstance(instance);
            System.out.println("PREDICTION = " + instances.getData().classAttribute().value((int) value));
        }
    }
}
