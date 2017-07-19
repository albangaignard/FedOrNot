/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import fr.univnantes.fedOrNot.Main;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class CLITest {

    public CLITest() {
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

    @Test
    @Ignore
    public void testHardcodedLabels() {
        String prefix = "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/FedOrNot-v2/code/SPARQLParser-master/Logs";
        String[] params = {
            "-i",
            prefix + "/Anapsid/virtuoso_CD1_EG_loop_1.log"
            + prefix + "/FedX/virtuoso_CD1_noCache_loop_1.log"
            + prefix + "/Single/dbpedia0-10000.log",
            "-l",
            "anapsid fedex single",
            "-o",
            "/tmp/out.arff"};

        Main.main(params);
    }

    @Test
    @Ignore
    public void testNewLabels() {
        String prefix = "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/FedOrNot-v2/code/SPARQLParser-master/Logs";
        String[] params = {
            "-i",
            prefix + "/Anapsid/virtuoso_CD1_EG_loop_1.log"
            + prefix + "/FedX/virtuoso_CD1_noCache_loop_1.log",
            "-l",
            "fed fed",
            "-o",
            "/tmp/out.arff"};
        Main.main(params);
    }

    @Test
//    @Ignore
    public void testNewLabelsV2() {
        String prefix = "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/FedOrNot-v2/code/SPARQLParser-master/Logs";
        String[] run1 = {
            "-i",
            prefix + "/Anapsid/virtuoso_CD1_EG_loop_1.log",
            "-v",
            "-ls",
            "fedx anapsid single",
            "-o",
            "/tmp/anapsid.arff"};

        String[] run2 = {
            "-i",
            prefix + "/FedX/virtuoso_CD1_noCache_loop_1.log",
            "-v",
            "-ls",
            "fedx anapsid single",
            "-l",
            "fedx",
            "-o",
            "/tmp/fedx.arff"};

        String[] run3 = {
            "-i",
            prefix + "/Single/dbpedia0-10000.log",
            "-s",
            "-ls",
            "fedx anapsid single",
            "-l",
            "single",
            "-o",
            "/tmp/single.arff"};

        String[] run4 = {
            "-m",
            "/tmp/fedx.arff /tmp/anapsid.arff /tmp/single.arff",
            "-n",
            "80 80 900",
            "-o",
            "/tmp/out.arff"};

        String[] run5 = {
            "-i",
            "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/SparqlFeatures/src/main/resources/Logs/access.log-20150818_test_lite.txt",
//            "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/SparqlFeatures/src/main/resources/Logs/microLog.txt",
            "-v",
            "-ls",
            "? fedx anapsid single",
            "-o",
            "/tmp/dbpedia.arff"};
        
        String[] run6 = {
            "-i",
            "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/SparqlFeatures/src/main/resources/Logs/bio2rdf-virtuoso-lite.log",
//            "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/SparqlFeatures/src/main/resources/Logs/microLog.txt",
            "-v",
            "-ls",
            "? fedx anapsid single",
            "-o",
            "/tmp/bio2rdf.arff"};
        
        String[] run7 = {
            "-i",
            prefix + "/Single/dbpedia0-10000.log",
            "-s",
            "-ls",
            "? fedx anapsid single",
            "-o",
            "/tmp/single.arff"};
        
        String[] run8 = {
            "-i",
            "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/SparqlFeatures/src/main/resources/Logs" 
                + "/2009-11-18-lite-1000Lines.log",
            "-v",
            "-ls",
            "? federated single",
            "-o",
            "/tmp/single.arff"};
        
        String[] run9 = {
            "-i",
            "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/2009-ONLY-SELECT" 
                + "/2009-11-26-ONLY-SELECT.log",
            "-v",
            "-ls",
            "? federated single",
            "-o",
            "/tmp/2009-11-26-ONLY-SELECT.arff"};
        
        
        String[] runUsewod = {
            "-i",
            "/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries" 
                + "/usewod2016-access.log-20150818_ONLY_SELECT_10K_lines.txt",
            "-v",
            "-ls",
            "? federated single",
            "-l", 
            "?",
            "-o",
            "/tmp/usewod.arff"};

//        Main.main(run1);
//        Main.main(run2);
//        Main.main(run3);
//        Main.main(run4);
//        Main.main(run5);
//        Main.main(run6);
//        Main.main(run7);
        Main.main(runUsewod);
    }
}
