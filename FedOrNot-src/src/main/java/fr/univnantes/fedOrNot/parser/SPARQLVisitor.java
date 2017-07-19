package fr.univnantes.fedOrNot.parser;

import fr.univnantes.fedOrNot.levenshtein.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.OpAssign;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpConditional;
import org.apache.jena.sparql.algebra.op.OpDatasetNames;
import org.apache.jena.sparql.algebra.op.OpDiff;
import org.apache.jena.sparql.algebra.op.OpDisjunction;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpExt;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpLabel;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpList;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpNull;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProcedure;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpQuad;
import org.apache.jena.sparql.algebra.op.OpQuadBlock;
import org.apache.jena.sparql.algebra.op.OpQuadPattern;
import org.apache.jena.sparql.algebra.op.OpReduced;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpSlice;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpTopN;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;

/**
 * An overrode OpVisitor to parse SPARQL algebra and extract features This
 * visitor come from JENA library. Please refer to JAVA visitor mechanisms for
 * more details.
 *
 * @author Jasone Lenormand
 * @see <a href="https://weka.wikispaces.com/Use+WEKA+in+your+Java+code>WEKA
 * Java lib</a>
 * @see <a href="https://sourcemaking.com/design_patterns/visitor/java/1>Visitor
 * Design Pattern</a> for a Java visitor example
 */
public class SPARQLVisitor implements OpVisitor {

    private boolean isASK;
    private boolean isSELECT;
    private boolean isCONSTRUCT;
    private double valLIMIT;
    private double valOFFSET;
    private int nbVarIncrements;
    private int nbORInFilters;
    private int nbNotEqualsInFilters;
    private int nbEqualsInFilters;
    private boolean usePrefix;
    private int nbGROUPBY;
    private int nbAND;
    private int nbUNION;
    private int nbFILTER;
    private int nbOPTIONAL;
    private int nbORDERBY;
    private int nbREGEX;
    private int nbBGP;
    private int nbTRIPLE;
    private int nbSERVICE;
    private int levenshtein;
    private int nbOperator;
    private String isFederated;

    public SPARQLVisitor(boolean ask, boolean sel, boolean cons, double limit, double offset, boolean pref) {
        isASK = ask;
        isSELECT = sel;
        isCONSTRUCT = cons;
        valLIMIT = limit;
        valOFFSET = offset;
        nbVarIncrements = 0;
        nbORInFilters = 0;
        nbNotEqualsInFilters = 0;
        nbEqualsInFilters = 0;
        usePrefix = pref;
        nbGROUPBY = 0;
        nbAND = 0;
        nbUNION = 0;
        nbFILTER = 0;
        nbOPTIONAL = 0;
        nbORDERBY = 0;
        nbREGEX = 0;
        nbBGP = 0;
        nbTRIPLE = 0;
        nbSERVICE = 0;
        levenshtein = 0;
        nbOperator = 0;
    }

    @Override
    public void visit(OpBGP arg0) {
        nbBGP += 1;
        nbTRIPLE += arg0.getPattern().size();
        List<Triple> tps = arg0.getPattern().getList();
        int nbTps = tps.size();
        if (nbTps > 1) {
            for (int i = 0; i < nbTps; ++i) {
                for (int j = i + 1; j < nbTps; ++j) {
                    levenshtein += LevenshteinDistance.levenshteinDistance(tps.get(i).toString(), tps.get(j).toString());
                }
            }
        }
        ++nbOperator;
    }

    @Override
    public void visit(OpQuadPattern arg0) {
        // TODO Auto-generated method stub
//        System.out.println(arg0.toString());
        ++nbOperator;
    }

    @Override
    public void visit(OpQuadBlock arg0) {
        // TODO Auto-generated method stub
//        System.out.println(arg0.toString());
        ++nbOperator;
    }

    @Override
    public void visit(OpTriple arg0) {
        // TODO Auto-generated method stub
//        System.out.println(arg0.toString());
        ++nbOperator;
    }

    @Override
    public void visit(OpQuad arg0) {
        // TODO Auto-generated method stub
//        System.out.println(arg0.toString());
        ++nbOperator;
    }

    @Override
    public void visit(OpPath arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpTable arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpNull arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpProcedure arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpPropFunc arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpFilter arg0) {
        for (Expr e : arg0.getExprs().getList()) {
            try {
                nbORInFilters += StringUtils.countMatches(e.toString(), "||");
                nbNotEqualsInFilters += StringUtils.countMatches(e.toString(), "!=");

                Pattern pattern = Pattern.compile("[^!]=");
                Matcher matcher = pattern.matcher(e.toString());
                while (matcher.find()) {
                    nbEqualsInFilters++;
                }
            } catch (Exception ex) {
                // try-catch for non-blocking SPARK tasks
            }

        }
        nbFILTER += 1;
        ++nbOperator;
    }

    @Override
    public void visit(OpGraph arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpService arg0) {
        nbSERVICE += 1;
        ++nbOperator;
    }

    @Override
    public void visit(OpDatasetNames arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpLabel arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpAssign arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpExtend arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpJoin arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpLeftJoin arg0) {
        nbOPTIONAL += 1;
        ++nbOperator;
    }

    @Override
    public void visit(OpUnion arg0) {
        nbUNION += 1;
        ++nbOperator;
    }

    @Override
    public void visit(OpDisjunction arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpDiff arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpMinus arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpConditional arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpSequence arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpExt arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpList arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpOrder arg0) {
        nbORDERBY += 1;
        ++nbOperator;
    }

    @Override
    public void visit(OpProject arg0) {
        ArrayList<Var> variables = (ArrayList<Var>) arg0.getVars();

        Pattern pattern = Pattern.compile("\\?[a-zA-Z]+_[0-9]+");
        for (Var v : variables) {
//            String s = v.toString();
            Matcher m = pattern.matcher(v.toString());
            if (m.matches()) {
                nbVarIncrements++;
            }
        }

//        System.out.println("----- nbVarIncrements = "+nbVarIncrements);
//        if (v.size() > 1) {
//            if (v.get(0).toString().length() > 2 && v.get(1).toString().length() > 2) {
//                String pref = "";
//                if (v.get(0).toString().substring(0, 3).equals(v.get(1).toString().substring(0, 3))) {
//                    pref = v.get(0).toString().substring(0, 3);
//                    for (Var str : v) {
//                        if (str.toString().length() > 2) {
//                            if (!str.toString().substring(0, 3).equals(pref)) {
//                                hasVarPref = false;
//                            }
//                        } else {
//                            hasVarPref = false;
//                        }
//                    }
//                } else {
//                    hasVarPref = false;
//                }
//            } else {
//                hasVarPref = false;
//            }
//        } else {
//            hasVarPref = false;
//        }
        ++nbOperator;
    }

    @Override
    public void visit(OpReduced arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpDistinct arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpSlice arg0) {
        ++nbOperator;
    }

    @Override
    public void visit(OpGroup arg0) {
        nbGROUPBY += 1;
        ++nbOperator;
    }

    @Override
    public void visit(OpTopN arg0) {
        ++nbOperator;
    }

    public boolean isASK() {
        return isASK;
    }

    public boolean isSELECT() {
        return isSELECT;
    }

    public boolean isCONSTRUCT() {
        return isCONSTRUCT;
    }

    public int getNbVarIncrements() {
        return nbVarIncrements;
    }

    public int getNbORInFilters() {
        return nbORInFilters;
    }

    public int getNbNotEqualsInFilters() {
        return nbNotEqualsInFilters;
    }

    public int getNbEqualsInFilters() {
        return nbEqualsInFilters;
    }

    public boolean usePrefix() {
        return usePrefix;
    }

    public int getNbAND() {
        return nbAND;
    }

    public int getNbUNION() {
        return nbUNION;
    }

    public int getNbFILTER() {
        return nbFILTER;
    }

    public int getNbOPTIONAL() {
        return nbOPTIONAL;
    }

    public int getNbORDERBY() {
        return nbORDERBY;
    }

    public double getValLIMIT() {
        return valLIMIT;
    }

    public double getValOFFSET() {
        return valOFFSET;
    }

    public int getNbREGEX() {
        return nbREGEX;
    }

    public int getNbBGP() {
        return nbBGP;
    }

    public int getNbTRIPLE() {
        return nbTRIPLE;
    }

    public int getNbSERVICE() {
        return nbSERVICE;
    }

    public int getNbGROUPBY() {
        return nbGROUPBY;
    }

    public String isFederated() {
        return isFederated;
    }

    public int getLevenshtein() {
        return levenshtein;
    }

    public int getNbOperator() {
        return nbOperator;
    }
}
