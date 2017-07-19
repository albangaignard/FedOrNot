/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univnantes.fedOrNot.parser;

import fr.univnantes.fedOrNot.Main.LogType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.jena.ext.com.google.common.base.Splitter;
import org.apache.log4j.Logger;
import weka.core.converters.ArffSaver;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class SparqlParserV2 {

    private static Logger logger = Logger.getLogger(SparqlParserV2.class);

    private List<String> classLabelSet = new ArrayList();

    public SparqlParserV2(List<String> labelSet) {
        this.classLabelSet = labelSet;

    }

//    public DataInstanceV2 parse(String inputPath, String label, LogType logType, int nbEntries) {
    public DataInstanceV3_noask parse(String inputPath, String label, LogType logType) {
        logger.info("Parsing : " + inputPath);
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
            if (logType == LogType.Virtuoso) {
                return parseVirtuosov2(br, inputPath, label);
            } else if (logType == logType.QueryList) {
                return parseQueryList(br, inputPath, label);
            }
        } catch (IOException e) {
            logger.error("Error: can't open the input file " + inputPath);
        }
        return null;
    }

    //    private DataInstanceV2 parseVirtuoso(BufferedReader br, String fileName, String label, int nbEntries) {
    private DataInstanceV3_noask parseVirtuosov2(BufferedReader br, String fileName, String label) {
        String line = null;

        DataInstanceV3_noask di = null;

        try {
            di = new DataInstanceV3_noask(fileName, this.classLabelSet);
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().contains("select")) {
                    String[] frag = line.split(" ");
                    for (String f : frag) {
                        if (f.toLowerCase().contains("/sparql")) {
                            try {
                                String query = f.split("\\?")[1];
                                final Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
                                if (map.get("query") != null) {
                                    String decodedQuery = URLDecoder.decode(map.get("query"), "UTF-8");
                                    di.addData(decodedQuery, label);
                                } else {
                                    logger.error("Impossible to get HTTP query param from " + f);
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                logger.error("Impossible to get sparql HTTP param from " + f);
                            } catch (IllegalArgumentException e) {
                                logger.error("Impossible to URLDecode SPARQL from " + f);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error: unknow error while reading the file " + fileName);
        }
        return di;

    }

//    private DataInstanceV2 parseVirtuoso(BufferedReader br, String fileName, String label, int nbEntries) {
    @Deprecated
    private DataInstanceV3_noask parseVirtuoso(BufferedReader br, String fileName, String label) {
        String line = null;
        String[] lineSplitted;

        DataInstanceV3_noask di = null;
        int nbAccol;
//        int i = 0;

        try {
            di = new DataInstanceV3_noask(fileName, this.classLabelSet);
            while ((line = br.readLine()) != null) {
                // Line pre-processing : 
                // for each lines, I start by extracting only the SPARQL query
                // I ignore every thing before a SELECT or and ASK or A CONSTRUCT
                // Then I count the number of '{' and '}' char to know when the SPARQL statement is closed
                line = URLDecoder.decode(line, "ASCII");
                StringBuilder queryType = new StringBuilder();
                StringBuilder queryBuilder = new StringBuilder();
                if (line.toLowerCase().contains("PREFIX".toLowerCase())) {
                    queryType.append("PREFIX");
                    lineSplitted = line.split("(?i)PREFIX"); // with case insensitivity
                    String sparqlQuery = lineSplitted[1].substring(0, lineSplitted[1].lastIndexOf("}") + 1);
                    queryBuilder.append(queryType.toString());
                    queryBuilder.append(sparqlQuery);

                    sparqlQuery = queryBuilder.toString();
                    if (sparqlQuery.toLowerCase().contains("SELECT".toLowerCase())
                            || sparqlQuery.toLowerCase().contains("ASK".toLowerCase())
                            || sparqlQuery.toLowerCase().contains("CONSTRUCT".toLowerCase())) {
                        di.addData(sparqlQuery, label);
                    } else {
                        logger.warn("Skipped query : ");
                        logger.warn(sparqlQuery);
                    }

                } else if (line.contains("SELECT") || line.contains("ASK") || line.contains("CONSTRUCT")) {
                    queryType.append(line.toLowerCase().contains("SELECT".toLowerCase()) ? "SELECT" : "");
                    queryType.append(line.toLowerCase().contains("ASK".toLowerCase()) ? "ASK" : "");
                    queryType.append(line.toLowerCase().contains("CONSTRUCT".toLowerCase()) ? "CONSTRUCT" : "");
                    // TODO handle PREFIX
                    lineSplitted = line.split("(?i)" + queryType.toString());
                    String sparqlQuery = lineSplitted[1].substring(0, lineSplitted[1].lastIndexOf("}") + 1);
                    queryBuilder.append(queryType.toString());
                    queryBuilder.append(sparqlQuery);
                    di.addData(queryBuilder.toString(), label);
                }
            }
        } catch (IOException e) {
            logger.error("Error: unknow error while reading the file " + fileName);
        }
        return di;
    }

    /**
     * Method parsing simple query logs
     * <p>
     * The method create a DataInstance, fill it with read lines and finally add
     * it to the "datas" vector</p>
     *
     * @param br the BufferedReader use to read the file
     * @param fileName the name of the input file
     */
    private DataInstanceV3_noask parseQueryList(BufferedReader br, String fileName, String label) {
        String line;
        DataInstanceV3_noask di = null;
        int i = 0;
        try {
            di = new DataInstanceV3_noask(fileName, this.classLabelSet); // see DataInstanceV2 class
            while ((line = br.readLine()) != null) {
//                logger.info("parsing : "+line);
                line = line.substring(1, line.length() - 1); // removing quotes (" ... ") surrounding the query

                try {
                    line = URLDecoder.decode(line, "UTF-8");
                    di.addData(line, label);
                    i++;
                } catch (IllegalArgumentException e) {
                    logger.warn("could not decode : ");
                    logger.warn(line);
                }
            }
        } catch (IOException e) {
            logger.error("Error: unknow error while reading the file " + fileName);
        }
        return di;
    }

    /**
     * Same that print all ARFF but only for DataInstanceV2 containing both
     * single and federated queries
     */
    public void saveAllMixedARFF(DataInstanceV3_noask di, File outputFile) {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(di.getData());
        try {
            saver.setFile(outputFile); //ARFF/test.arff
            saver.writeBatch();
        } catch (IOException e) {
            logger.error("Can't write outputFile " + outputFile);
        }
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
}
