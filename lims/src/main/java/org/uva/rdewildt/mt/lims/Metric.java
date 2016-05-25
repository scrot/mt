package org.uva.rdewildt.mt.lims;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/22/16.
 */
public class Metric {
    private final String classname;

    //CK-metrics
    private final int wmc;
    private final int noc;
    private final int rfc;
    private final int cbo;
    private final int dit;
    private final int lcom;

    //Li-Metrics
    private final int mpc;
    private final int dac;
    private final int nom;
    private final int size1;
    private final int size2;

    public Metric(Metric metric){
        this.classname = metric.getClassname();
        this.wmc = metric.getWmc();
        this.noc = metric.getNoc();
        this.rfc = metric.getRfc();
        this.cbo = metric.getCbo();
        this.dit = metric.getDit();
        this.lcom = metric.getLcom();
        this.mpc = metric.getMpc();
        this.dac = metric.getDac();
        this.nom = metric.getNom();
        this.size1 = metric.getSize1();
        this.size2 = metric.getSize2();

    }

    public Metric(String classname, int wmc, int noc, int rfc, int cbo, int dit,
                  int lcom, int mpc, int dac, int nom, int size1,
                  int size2) {
        this.classname = classname;
        this.wmc = wmc;
        this.noc = noc;
        this.rfc = rfc;
        this.cbo = cbo;
        this.dit = dit;
        this.lcom = lcom;
        this.mpc = mpc;
        this.dac = dac;
        this.nom = nom;
        this.size1 = size1;
        this.size2 = size2;
    }

    public static List<String> getMetricNames(){
        return new ArrayList<String>(){{
            add("Class"); add("WMC"); add("DIT"); add("NOC");
            add("CBO"); add("RFC"); add("LCOM"); add("DAC");
            add("MPC"); add("NOM"); add("SIZE1"); add("SIZE2");
        }};
    }

    public Map<String, Object> getMetrics(){
        return new LinkedHashMap<String, Object>(){{
            put("Class", getClassname());
            put("WMC", getWmc());
            put("DIT", getDit());
            put("NOC", getNoc());
            put("CBO", getCbo());
            put("RFC", getRfc());
            put("LCOM", getLcom());
            put("DAC", getDac());
            put("MPC", getMpc());
            put("NOM", getNom());
            put("SIZE1", getSize1());
            put("SIZE2", getSize2());
        }};
    }

    public String getClassname() {
        return classname;
    }

    public int getWmc() {
        return wmc;
    }

    public int getNoc() {
        return noc;
    }

    public int getRfc() { return rfc; }

    public int getCbo() {
        return cbo;
    }

    public int getDit() {
        return dit;
    }

    public int getLcom() {
        return lcom;
    }

    public int getMpc() {
        return mpc;
    }

    public int getDac() {
        return dac;
    }

    public int getNom() {
        return nom;
    }

    public int getSize1() { return size1; }

    public int getSize2() { return size2; }
}
