/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univnantes.fedOrNot.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class ArffMerger {

    private List<File> inFiles;
    private List<Integer> nbEntries;
    private File outFile;

    public ArffMerger(List<File> inFiles, List<Integer> nbEntries, File outputFile) {
        this.inFiles = inFiles;
        this.nbEntries = nbEntries;
        this.outFile = outputFile;
    }

    public void mergeAndRandomize() {
        List<Instances> listOfInstances = new ArrayList<>();

//        FastVector classLabels = new FastVector();
//        for (String l : this.labels) {
//            System.out.println("Setting " + l);
//            if (!classLabels.contains(l)) {
//                classLabels.addElement(l);
//            }
//        }
//        Attribute classAtt = new Attribute("Class", classLabels);

        for (File f : inFiles) {
            try {
                Instances i = new Instances(new FileReader(f));

//                Random r = i.getRandomNumberGenerator(99L);
//                i.randomize(r);
                listOfInstances.add(i);

            } catch (IOException ex) {
                System.out.println("Error while reading " + f.getAbsolutePath());
                System.exit(1);
            }
        }

        Instances res = new Instances(listOfInstances.get(0), 0, 0);
//        Attribute att2 = new Attribute("Query", (FastVector) null);

        for (int i = 0; i < listOfInstances.size(); i++) {
            int count = 0;
            Enumeration e = listOfInstances.get(i).enumerateInstances();
            while (e.hasMoreElements() && (count < this.nbEntries.get(i))) {
                res.add((Instance) e.nextElement());
                count++;
            }
        }

        ArffSaver saver = new ArffSaver();
        saver.setInstances(res);
        try {
            saver.setFile(this.outFile); //ARFF/test.arff
            saver.writeBatch();
        } catch (IOException ex) {
            System.out.println("Can't write the merged outputFile");
        }
    }

}
