/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.jena.ext.com.google.common.base.Splitter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class LogParsingTest {

    public LogParsingTest() {
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

    //
    @Test
    public void hello() throws URISyntaxException, UnsupportedEncodingException {
        String line = "0.0.0.0 - - [18/Nov/2009:00:00:48 -0800] \"GET /sparql/?query=SELECT+%3Fabstract+WHERE+{+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FC4H8O%3E+%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2Fabstract%3E+%3Fabstract.+FILTER+langMatches(lang(%3Fabstract)%2C+%27en%27)+}&format=json HTTP/1.1\" 200 119 \"\" \"PEAR HTTP_Request class ( http://pear.php.net/ )\" \"US\" \"5f27717cf6283a63fd7e6ab5b00a5d594374d640\"";
        String[] frag = line.split(" ");
        for (String f : frag) {
            if (f.toLowerCase().contains("/sparql")) {
                System.out.println(f);
                String query = f.split("\\?")[1];
                final Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
                System.out.println(URLDecoder.decode(map.get("query"), "UTF-8"));
            }
        }

    }
}
