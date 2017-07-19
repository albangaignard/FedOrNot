/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import fr.univnantes.fedOrNot.parser.SPARQLParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
@Deprecated
public class LogAnalysisTest {

    public LogAnalysisTest() {
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
    public static File getTmpFile(String fileNameInClassPath) throws IOException {
        File logFile = File.createTempFile("log", ".log");
        String inPath = logFile.getAbsolutePath();
        InputStream in = LogAnalysisTest.class.getClassLoader().getResourceAsStream(fileNameInClassPath);
        FileOutputStream fout = new FileOutputStream(logFile);
        IOUtils.copyLarge(in, fout);
        fout.flush();
        fout.close();
        System.out.println("Written "+inPath);
        return logFile;
    }

    @Test
    @Ignore
    public void hello() throws IOException {

        File anapsidLog = getTmpFile("Logs/Anapsid/virtuoso_CD1_EG_loop_1.log");
        File fedxLog = getTmpFile("Logs/FedX/virtuoso_CD1_noCache_loop_1.log");
        File singleLog = getTmpFile("Logs/Single/dbpedia0-10000.log");

        SPARQLParser sp = new SPARQLParser();
        sp.parse(anapsidLog.getAbsolutePath(), "anapsid");
        sp.parse(fedxLog.getAbsolutePath(), "fedex");
        sp.parse(singleLog.getAbsolutePath(), "single");
        
        sp.genMixed(10, 40, 50);
        sp.printAllMixedARFF();

//        SPARQLParser sp = new SPARQLParser();
//        sp.parseV0("/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/FedOrNot-v2/code/SPARQLParser-master/Logs/Anapsid/virtuoso_CD1_EG_loop_1.log", "anapsid");
//        sp.parseV0("/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/FedOrNot-v2/code/SPARQLParser-master/Logs/FedX/virtuoso_CD1_noCache_loop_1.log", "fedx");
//        sp.parseV0("/Users/gaignard-a/Documents/Projets/Stages-M1-M2/2016-M1-GDD-federated-queries/FedQueriesOrNot/FedOrNot-v2/code/SPARQLParser-master/Logs/Single/dbpedia0-10000.log", "single");
//        sp.genMixed(3, 3, 3);
//        sp.printAllMixedARFF();

    }
}
