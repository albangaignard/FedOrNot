package fr.univnantes.fedOrNot;

import fr.univnantes.fedOrNot.parser.ArffMerger;
import fr.univnantes.fedOrNot.parser.DataInstanceV3_noask;
import fr.univnantes.fedOrNot.parser.SparqlParserV2;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

public class Main {

    public static enum LogType {
        Virtuoso, QueryList
    };

    private static Logger logger = Logger.getLogger(Main.class);
    private static QueryLog queryLog = new QueryLog();
    private static File outFile;

    /**
     * TODO ./genSparqlFeatures -in queryLog1.log queryLog2.log queryLog3.log
     * -class label1 label2 label1 -o out.arff
     *
     */
    /**
     * A simple example of parsing and generating an ARFF file
     *
     * @param args Params passed to the main during execution
     */
    public static void main(String[] args) {

        Options options = new Options();
        Option inLogsOpt = new Option("i", "queryLog", true, "input query log");
        Option inLabelsOpt = new Option("l", "logLabel", true, "query log label");

        Option outArffOpt = new Option("o", "outArffFile", true, "ARFF output file");

//        Option mergeArffOpt = new Option("m", "mergeArffFiles", true, "ARFF files to be merged");
//        mergeArffOpt.setValueSeparator(' ');
//        mergeArffOpt.setArgs(Option.UNLIMITED_VALUES);
        Option labelSetOpt = new Option("ls", "labelSet", true, "list of labels, with ? as mandatory label");
        labelSetOpt.setValueSeparator(' ');
        labelSetOpt.setArgs(Option.UNLIMITED_VALUES);
//        Option inNbOpt = new Option("n", "entryNumbers", true, "numbers of query entries");
//        inNbOpt.setValueSeparator(' ');
//        inNbOpt.setArgs(Option.UNLIMITED_VALUES);

        Option virtuosoLogTypeOpt = new Option("v", "virtuoso", false, "to parse Virtuoso logs");
        Option singleLogTypeOpt = new Option("s", "single", false, "to parse Single logs");

        options.addOption(inLogsOpt);
        options.addOption(inLabelsOpt);
//        options.addOption(inNbOpt);
        options.addOption(outArffOpt);
        options.addOption(virtuosoLogTypeOpt);
        options.addOption(singleLogTypeOpt);
//        options.addOption(mergeArffOpt);
        options.addOption(labelSetOpt);

        String header = "genSparqlFeatures is feature extraction tool from SPARQL query logs. Weka ARFF files are produced as results.";
        String footer = "\nPlease report any issue to alban.gaignard@univ-nantes.fr";

        try {
            CommandLineParser parser = new BasicParser();
            CommandLine cmd = parser.parse(options, args);

            if ((cmd.hasOption("m")) && (cmd.hasOption("n")) && (cmd.hasOption("o"))) {
                String[] toBeMergedFiles = cmd.getOptionValues("m");
                String[] nbEntriesS = cmd.getOptionValues("n");

                File out = new File(cmd.getOptionValue("o"));

                List<File> inputs = new ArrayList();
                for (String s : toBeMergedFiles) {
                    inputs.add(new File(s));
                }
                List<Integer> nbEntries = new ArrayList();
                for (String s : nbEntriesS) {
                    nbEntries.add(Integer.parseInt(s));
                }

                ArffMerger merger = new ArffMerger(inputs, nbEntries, out);
                merger.mergeAndRandomize();

            } else if ((cmd.hasOption("i")) && (cmd.hasOption("ls")) && (cmd.hasOption("o"))) {
                String[] labelsS = cmd.getOptionValues("ls");
                List<String> labels = new ArrayList();
                for (String s : labelsS) {
                    labels.add(s);
                }
                Main.queryLog.setLabelSet(labels);

                // Default log type
                Main.queryLog.setLogType(LogType.Virtuoso);
                if (cmd.hasOption("v")) {
                    Main.queryLog.setLogType(LogType.Virtuoso);
                } else if (cmd.hasOption("s")) {
                    Main.queryLog.setLogType(LogType.QueryList);
                } else {
                    logger.warn("No log type specified (-s or -v). Considering Virtuoso logs as default.");
                    Main.queryLog.setLogType(LogType.Virtuoso);
                }

                Main.queryLog.setFilePath(cmd.getOptionValue("i"));
                if ((cmd.hasOption("l"))) {
                    Main.queryLog.setLabel(cmd.getOptionValue("l"));
                } else {
                    Main.queryLog.setLabel("?");
                }
                String outPutFile = cmd.getOptionValue("o");
                Path p = FileSystems.getDefault().getPath(outPutFile);
                if (p.toFile().exists()) {
                    System.out.println(outPutFile + " already exists ! Please specify another output file.");
                    System.exit(0);
                }
                Main.outFile = p.toFile();

                // parsing and generation of the ARFF file
//        ArrayList<String> labels = new ArrayList<>();
//        labels.add("fedx");
//        labels.add("anapsid");
//        labels.add("federated");
//        labels.add("single");
                SparqlParserV2 sp = new SparqlParserV2(Main.queryLog.getLabelSet());
                DataInstanceV3_noask di = sp.parse(Main.queryLog.getFilePath(), Main.queryLog.getLabel(), Main.queryLog.getLogType());
                sp.saveAllMixedARFF(di, outFile);

            } else {
                System.out.println("Missing -i -l or -o option.");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("genSparqlFeatures", header, options, footer, true);
                System.exit(0);
            }
//            else {
//                System.out.println("Missing -m, -n or -o option !");
//                HelpFormatter formatter = new HelpFormatter();
//                formatter.printHelp("genSparqlFeatures", header, options, footer, true);
//                System.exit(0);
//            }

        } catch (ParseException e) {
            System.out.println("Command line parsing error.");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("genSparqlFeatures", header, options, footer, true);
            System.exit(0);
        }

    }
}
