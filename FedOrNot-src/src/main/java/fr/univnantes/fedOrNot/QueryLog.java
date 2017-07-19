/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univnantes.fedOrNot;

import fr.univnantes.fedOrNot.Main.LogType;
import java.util.List;

/**
 *
 * @author Alban Gaignard <alban.gaignard@cnrs.fr>
 */
public class QueryLog {
    private String filePath;
    private String label;
    private List<String> labelSet;
    private Integer nbEntries;
    private LogType logType;
    
    public QueryLog() {
        //Default values
        this.logType = LogType.Virtuoso;
        this.nbEntries = 1;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getNbEntries() {
        return nbEntries;
    }

    public void setNbEntries(Integer nbEntries) {
        this.nbEntries = nbEntries;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }   

    public List<String> getLabelSet() {
        return labelSet;
    }

    public void setLabelSet(List<String> labelSet) {
        this.labelSet = labelSet;
    }

}
