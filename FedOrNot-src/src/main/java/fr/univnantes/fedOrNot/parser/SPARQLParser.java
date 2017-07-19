package fr.univnantes.fedOrNot.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import weka.core.converters.ArffSaver;

/**
 * The class the user will interact with.
 * <p>
 * SPARQLParser contains every methods to parse a log file and generate the ARFF
 * associated file</p>
 * <p>
 * Only logs from <a href="lsq.aksw.org/sparql">Virtuoso SPARQL Query Editor</a>
 * are supported</p>
 *
 * @author Jasone Lenormand
 * @see DataInstance
 */
@Deprecated
public class SPARQLParser {

    private ArrayList<DataInstance> datas; // All parsed files as DataInstance
    private String destinationFolder = "ARFF/"; // ARFF generated files destination folder
    // TODO allow user to choose the destination folder
    private int nbAnapsidEntries; // the total number of ANAPSID parsed queries
    private int nbFedxEntries; // the total number of FedX parsed queries
    private int nbSingleEntries; // the total number of ANAPSID parsed queries

    /**
     * SPARLParser constructor
     */
    public SPARQLParser() {
        datas = new ArrayList<DataInstance>();
        nbAnapsidEntries = 0;
        nbFedxEntries = 0;
        nbSingleEntries = 0;
    }

    /**
     * The only parsing method visible by user.
     * <p>
     * This method calls the right parsing strategy according the the logType
     * entered by user
     *
     * @param inputPath The path of the file to parse (has to be a query-engine
     * log file)
     * @param logType The type of the input log file (has to be single, fedx or
     * anapsid)
     */
    public void parse(String inputPath, String logType) {
        //TODO use Strategy Design Pattern instead of 2 parsing methods
        System.out.println("Parsing : " + inputPath);
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
            switch (logType) {
                case "anapsid":
                    parseVirtuoso(br, inputPath, logType);
                    break;
                case "fedex":
                    parseVirtuoso(br, inputPath, logType);
                    break;
                case "single":
                    parseSingle(br, inputPath);
                    break;
                default:
                    System.out.println("The parser don't know how to parse your query engine");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: can't open the input file");
        }
    }

    /**
     * Method parsing single queries CSV log file gathered from virtuoso
     * <p>
     * The method create a DataInstance, fill it with read lines and finally add
     * it to the "datas" vector</p>
     *
     * @param br the BufferedReader use to read the file
     * @param fileName the name of the input file
     */
    private void parseSingle(BufferedReader br, String fileName) {
        String line;
        DataInstance di;
        try {
            di = new DataInstance(fileName); // see DataInstance class
            line = br.readLine(); // skipping first line which always contains the String "query"
            while ((line = br.readLine()) != null) {
                line = line.substring(1, line.length() - 1); // removing quotes (" ... ") surrounding the query
                di.addData(line, "single");
            }
            datas.add(di);
            nbSingleEntries += di.getNbEntries(); // update the total number of single Entries (sum of all single data)
        } catch (IOException e) {
            System.out.println("Error: unknow error while reading the file");
        }

    }

    /**
     * Method parsing sub federated queries provided by the Lina research
     * laboratory
     * <p>
     * These queries needs to be cleaned up from every unused informations
     * before being extracted
     * <p>
     * The method create a DataInstance, fill it with read lines and finally add
     * it to the "datas" vector</p>
     *
     * @param br the BufferedReader use to read the file
     * @param fileName the name of the input file
     * @param queryEngineName could be fedx or anapsid
     */
    private void parseVirtuoso(BufferedReader br, String fileName, String queryEngineName) {
        String line;
        String[] lineSplitted;
        StringBuilder queryType;
        StringBuilder queryBuilder;
        DataInstance di;
        int nbAccol;

        try {
            di = new DataInstance(fileName);
            while ((line = br.readLine()) != null) {
                // for each lines, I start by extracting only the SPARQL query
                // I ignore every thing before a SELECT or and ASK or A CONSTRUCT
                // Then I count the number of '{' and '}' char to know when the SPARQL statement is closed
                if (line.contains("SELECT") || line.contains("ASK") || line.contains("CONSTRUCT")) {
                    queryType = new StringBuilder();
                    queryBuilder = new StringBuilder();
                    queryType.append(line.contains("SELECT") ? "SELECT" : "");
                    queryType.append(line.contains("ASK") ? "ASK" : "");
                    queryType.append(line.contains("CONSTRUCT") ? "CONSTRUCT" : "");
                    lineSplitted = line.split(queryType.toString());
                    queryBuilder.append(queryType.toString());
                    queryBuilder.append(lineSplitted[1]);
                    nbAccol = accolCount(lineSplitted[1]);
                    while (nbAccol > -1) { // when =-1 : we read the } of 'sparql{' token
                        line = br.readLine();
                        queryBuilder.append(line);
                        nbAccol += accolCount(line);
                    }
                    queryBuilder.deleteCharAt(queryBuilder.length() - 1); // remove the last '}' which is not a member of the SPARQL query
                    di.addData(queryBuilder.toString(), queryEngineName);
                }
            }
            datas.add(di);
            if (queryEngineName.equals("fedx")) {
                nbFedxEntries += di.getNbEntries();
            }
            if (queryEngineName.equals("anapsid")) {
                nbAnapsidEntries += di.getNbEntries();
            }

        } catch (IOException e) {
            System.out.println("Error: unknow error while reading the file");
        }
    }

    /**
     * Print the ARFF file from the datas vector on the chose index to the
     * provided file path
     *
     * @param ouputPath The output file path
     * @param index The index of the DataInstance to print
     * @see <a href="https://weka.wikispaces.com/Use+WEKA+in+your+Java+code>WEKA
     * Java lib</a>
     */
    public void printARFF(String ouputPath, int index) {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(datas.get(index).getData());
        try {
            saver.setFile(new File(ouputPath)); // ARFF/test.arff
            saver.writeBatch();
        } catch (IOException e) {
            System.out.println("Can't read the outputFile");
        }
    }

    /**
     * Same than printARFF but for every datas vector instances (one ARFF file
     * per DataInstance)
     * <p>
     * The name is automatically built according to the current instance
     * type</p>
     */
    public void printAllARFF() {
        ArffSaver saver = new ArffSaver();
        StringBuilder outputName;
        int instanceIndex = 0; // used to ensure that files names are unique
        for (DataInstance di : datas) {
            // output file name building
            outputName = new StringBuilder();
            outputName.append(destinationFolder);
            outputName.append("Instance_");
            outputName.append(instanceIndex);
            outputName.append("_");
            outputName.append(di.getInputName());
            outputName.append("_");
            outputName.append(di.getFedType());
            outputName.append(".arff");

            saver.setInstances(di.getData());
            try {
                saver.setFile(new File(outputName.toString())); //ARFF/test.arff
                saver.writeBatch();
            } catch (IOException e) {
                System.out.println("Can't read the outputFile");
            }
            ++instanceIndex;
        }
    }

    /**
     * Same that print all ARFF but only for DataInstance containing both single
     * and federated queries
     */
    public void printAllMixedARFF() {
        ArffSaver saver = new ArffSaver();
        StringBuilder outputName;
        int instanceIndex = 0;
        for (DataInstance di : datas) {
            if (di.getFedType().equals("mixed")) {
                outputName = new StringBuilder();
                outputName.append(destinationFolder);
                outputName.append("Instance_");
                outputName.append(instanceIndex);
                outputName.append("_");
                outputName.append(di.getInputName());
                outputName.append("_");
                outputName.append(di.getFedType());
                outputName.append(".arff");

                saver.setInstances(di.getData());
                try {
                    saver.setFile(new File(outputName.toString())); //ARFF/test.arff
                    saver.writeBatch();
                } catch (IOException e) {
                    System.out.println("Can't read the outputFile");
                }
                ++instanceIndex;
            }
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

    /**
     * Generate a DataInstance containing the number of each data type according
     * to parameters The generated instance is pushed at the end of the 'datas'
     * vector Selected entries are the X first in the 'datas' vector (starting
     * at indexe 0) ARFF generated by this methods are the ones used in WEKA for
     * experimentations
     *
     * @param nbFedX The number of FedX entries
     * @param nbAnapsid The number of anapsid entries
     * @param nbSingle The number of Single entries
     */
    public void genMixed(int nbFedX, int nbAnapsid, int nbSingle) {

        int currIndexDatas = 0;
        int currIndexData = 0;
        int remainingDatas;
        DataInstance currentDi;
        DataInstance mixedDi = new DataInstance(nbFedX + "OfFedex_" + nbAnapsid + "OfAnapsid_" + nbSingle + "OfSingle");

        // takes the nbFedX first FedX queries of 'datas' vector and put them in mixedDi
        while (nbFedX > 0) {
//            System.out.println("---");
//            System.out.println(datas.size() + "/" + currIndexDatas);
//            System.out.println(nbFedX);
//            System.out.println("---");
            // search for fedx entries
            if (datas.get(currIndexDatas).getFedType().equals("fedex")) {
                currentDi = datas.get(currIndexDatas);
                remainingDatas = currentDi.getNbEntries();
                currIndexData = 0;
                while (remainingDatas > 0 && nbFedX > 0) {
                    mixedDi.addData(currentDi.getInstance(currIndexData), "mixed");
                    currIndexData += 1;
                    remainingDatas -= 1;
                    nbFedX -= 1;
                }
            }
            currIndexDatas += 1;
        }

        // takes the nbAnapsid first anapsid queries of 'datas' vector and put them in mixedDi
        currIndexDatas = 0;
        while (nbAnapsid > 0) {
            if (datas.get(currIndexDatas).getFedType().equals("anapsid")) {
                currentDi = datas.get(currIndexDatas);
                remainingDatas = currentDi.getNbEntries();
                currIndexData = 0;
                while (remainingDatas > 0 && nbAnapsid > 0) {
                    mixedDi.addData(currentDi.getInstance(currIndexData), "mixed");
                    currIndexData += 1;
                    remainingDatas -= 1;
                    nbAnapsid -= 1;
                }
            }
            currIndexDatas += 1;
        }

        // takes the nbSingle first Single queries of 'datas' vector and put them in mixedDi
        currIndexDatas = 0;
        while (nbSingle > 0) {
            if (datas.get(currIndexDatas).getFedType().equals("single")) {
                currentDi = datas.get(currIndexDatas);
                remainingDatas = currentDi.getNbEntries();
                currIndexData = 0;
                while (remainingDatas > 0 && nbSingle > 0) {
                    mixedDi.addData(currentDi.getInstance(currIndexData), "mixed");
                    currIndexData += 1;
                    remainingDatas -= 1;
                    nbSingle -= 1;
                }
            }
            currIndexDatas += 1;
        }
        datas.add(mixedDi); // add mixedDi to datas
    }

    public DataInstance getDataInstance(int index) {
        return datas.get(index);
    }

    public int getTotalEntries() {
        return nbAnapsidEntries + nbFedxEntries + nbSingleEntries;
    }

    public int getNbFedex() {
        return nbFedxEntries;
    }

    public int getNbAnapsid() {
        return nbAnapsidEntries;
    }

    public int getNbSingle() {
        return nbSingleEntries;
    }
}
