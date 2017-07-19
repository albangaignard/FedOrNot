/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Alban Gaignard <alban.gaignard@univ-nantes.fr>
 */
public class VarNameRegex {

    public VarNameRegex() {
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
    public void hello() {
        Pattern pattern = Pattern.compile("\\?[a-zA-Z]+_[0-9]+");

        Matcher m1 = pattern.matcher("?o_22");
        Matcher m2 = pattern.matcher("?ab_254");
        Matcher m3 = pattern.matcher("?AB_2");
        Matcher m4 = pattern.matcher("?xY_1092");
        Assert.assertTrue(m1.matches());
        Assert.assertTrue(m2.matches());
        Assert.assertTrue(m3.matches());
        Assert.assertTrue(m4.matches());
        
        Matcher m5 = pattern.matcher("?xY");
        Matcher m6 = pattern.matcher("?x54");
        Assert.assertFalse(m5.matches());
        Assert.assertFalse(m6.matches());
    }
}
