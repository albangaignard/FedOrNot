/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import fr.univnantes.fedOrNot.parser.ArffMerger;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class MergeTest {

    public MergeTest() {
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
    public void mergeTest() {
        String[] inputsS = "/tmp/single.arff /tmp/anapsid.arff /tmp/fedx.arff".split(" ");
        String[] nbEntriesS = "10 4 4".split(" ");
        File out = new File("/tmp/out.arff");

        List<File> inputs = new ArrayList();
        for (String s : inputsS) {
            inputs.add(new File(s));
        }
        List<Integer> nbEntries = new ArrayList();
        for (String s : nbEntriesS) {
            nbEntries.add(Integer.parseInt(s));
        }

        ArffMerger merger = new ArffMerger(inputs,nbEntries, out);
        merger.mergeAndRandomize();
    }
}
