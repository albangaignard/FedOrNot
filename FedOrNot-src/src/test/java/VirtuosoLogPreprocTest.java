/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import fr.univnantes.fedOrNot.parser.SparqlParserV2;
import fr.univnantes.fedOrNot.parser.Util;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.Prologue;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class VirtuosoLogPreprocTest {

    private static Logger logger = Logger.getLogger(VirtuosoLogPreprocTest.class);

    public VirtuosoLogPreprocTest() {
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
    public void hello() throws UnsupportedEncodingException {
        //with upper case
        String line0 = "9347927e0f5975e8ef31f7899097c109 - - [17/Aug/2015 03:00:00 +0200] \"GET /sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&output=json&query=PREFIX%20rdfs%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%20SELECT%20*%20WHERE%20%7B%20%3Fcity%20a%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2FPlace%3E%3B%20rdfs%3Alabel%20%27Polk%27%40en.%20%20%3Fairport%20a%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2FAirport%3E.%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Fcity%3E%20%3Fcity%7D%20UNION%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Flocation%3E%20%3Fcity%7D%20UNION%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2FcityServed%3E%20%3Fcity.%7D%20UNION%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Fcity%3E%20%3Fcity.%20%7D%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2Fiata%3E%20%3Fiata.%7D%20UNION%20%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2FiataLocationIdentifier%3E%20%3Fiata.%20%7D%20OPTIONAL%20%7B%20%3Fairport%20foaf%3Ahomepage%20%3Fairport_home.%20%7D%20OPTIONAL%20%7B%20%3Fairport%20rdfs%3Alabel%20%3Fname.%20%7D%20OPTIONAL%20%7B%20%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2Fnativename%3E%20%3Fairport_name.%7D%20FILTER%20%28%20%21bound%28%3Fname%29%20%7C%7C%20langMatches%28%20lang%28%3Fname%29%2C%20%27en%27%29%20%29%7D HTTP/1.1\" 200 174 \"-\" \"R\" \"-\"\n";

        //with lower case
        String line = "9347927e0f5975e8ef31f7899097c109 - - [17/Aug/2015 03:00:00 +0200] \"GET /sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&output=json&query=prefix%20rdfs%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%20select%20*%20WHERE%20%7B%20%3Fcity%20a%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2FPlace%3E%3B%20rdfs%3Alabel%20%27Polk%27%40en.%20%20%3Fairport%20a%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2FAirport%3E.%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Fcity%3E%20%3Fcity%7D%20UNION%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Flocation%3E%20%3Fcity%7D%20UNION%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2FcityServed%3E%20%3Fcity.%7D%20UNION%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Fcity%3E%20%3Fcity.%20%7D%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2Fiata%3E%20%3Fiata.%7D%20UNION%20%20%7B%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2FiataLocationIdentifier%3E%20%3Fiata.%20%7D%20OPTIONAL%20%7B%20%3Fairport%20foaf%3Ahomepage%20%3Fairport_home.%20%7D%20OPTIONAL%20%7B%20%3Fairport%20rdfs%3Alabel%20%3Fname.%20%7D%20OPTIONAL%20%7B%20%3Fairport%20%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2Fnativename%3E%20%3Fairport_name.%7D%20FILTER%20%28%20%21bound%28%3Fname%29%20%7C%7C%20langMatches%28%20lang%28%3Fname%29%2C%20%27en%27%29%20%29%7D HTTP/1.1\" 200 174 \"-\" \"R\" \"-\"\n";

        System.out.println(line);
        System.out.println("");

        line = URLDecoder.decode(line, "ASCII");
        System.out.println(line);
        System.out.println("");

        System.out.println("nb braces = " + accolCount(line));
        System.out.println("nb opened braces = " + openedBraceCount(line));

        StringBuilder queryType = new StringBuilder();
        StringBuilder queryBuilder = new StringBuilder();

        String[] lineSplitted;

        if (line.toLowerCase().contains("PREFIX".toLowerCase())) {
            queryType.append("PREFIX");
            lineSplitted = line.split("(?i)PREFIX");
        } else {
            queryType.append(line.toLowerCase().contains("SELECT".toLowerCase()) ? "SELECT" : "");
            queryType.append(line.toLowerCase().contains("ASK".toLowerCase()) ? "ASK" : "");
            queryType.append(line.toLowerCase().contains("CONSTRUCT".toLowerCase()) ? "CONSTRUCT" : "");
            // TODO handle PREFIX
            lineSplitted = line.split("(?i)" + queryType.toString());
        }

        queryBuilder.append(queryType.toString());
        String q = lineSplitted[1].substring(0, lineSplitted[1].lastIndexOf("}") + 1);

        queryBuilder.append(q);

        String query = queryBuilder.toString();

        logger.info("----------------");
        logger.info(query);

        Prologue p = Util.getPrologue();
        logger.info(p.toString());

        try {
            Query jenaQ = new Query(p);
            Query parsedQ = QueryFactory.parse(jenaQ, query, null, Syntax.syntaxSPARQL_11);
            logger.info(parsedQ.toString());
        } catch (QueryParseException e) {
            logger.error("Impossible to parse " + query);
            logger.error("Skipped entry !");
            e.printStackTrace();
            return;
        }

//        SparqlParserV2 sp = new SparqlParserV2(Arrays.asList("fedx anapsid single".split(" ")));
    }

    /**
     * Method used locally in the federated parsing strategy Count the number of
     * '{' and '}' in a String
     *
     * @param line the String when '{' and '}' are counted
     * @return the number of '{' and '}' in a given String
     */
    private int accolCount(String line) {
        int accols = 0;
        for (Byte b : line.getBytes()) {
            if (b.equals((byte) '{')) {
                accols += 1;
            }
            if (b.equals((byte) '}')) {
                accols -= 1;
            }
        }
        return accols;
    }

    private int openedBraceCount(String line) {
        int accols = 0;
        for (Byte b : line.getBytes()) {
            if (b.equals((byte) '{')) {
                accols += 1;
            }
        }
        return accols;
    }
}
