/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univnantes.fedOrNot.parser;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.jena.sparql.core.Prologue;
import org.apache.log4j.Logger;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class Util {
    private static Prologue prologue;
    private static Logger logger = Logger.getLogger(Util.class);
    
    private Util() {
        prologue = initPrologue();
    }

    private static Util INSTANCE = null;

    public static Util getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Util();
        }
        return INSTANCE;
    }

    public static Prologue getPrologue() {
        if (INSTANCE == null) {
            INSTANCE = new Util();
        }
        return prologue;
    }
    
    private Prologue initPrologue() {
          // Loading SPARQL namespace PREFIXs
        Properties namespaces = new Properties();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("namespaces.properties");
        try {
            namespaces.load(in);
        } catch (IOException e) {
            logger.error("Impossible to load namespaces.properties file !");
            logger.error("Exiting");
            System.exit(-1);
        }
        Prologue queryPrologue = new Prologue();
        for (Object n : namespaces.keySet()) {
            String pName = (String) n;
            queryPrologue.setPrefix(pName, namespaces.getProperty(pName));
        }
        return queryPrologue;
    }
}
