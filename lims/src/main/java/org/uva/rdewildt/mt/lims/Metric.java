package org.uva.rdewildt.mt.lims;

import org.uva.rdewildt.mt.report.Reportable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/22/16.
 */
public class Metric implements Reportable {

    private String classname;

    //CK-metrics
    private int wmc;
    private int noc;
    private int rfc;
    private int cbo;
    private int dit;
    private int lcom;

    //Li-Metrics
    private int mpc;
    private int dac;
    private int nom;
    private int size1;
    private int size2;

    public Metric(){
        this("");
    }

    public Metric(String classname){
        this(classname,0,0,0,0,0,0,0,0,0,0,0);

    }

    public Metric(Metric metric){
        this(metric.getClassname(), metric.getWmc(), metric.getNoc(),
        metric.getRfc(), metric.getCbo(), metric.getDit(),
        metric.getLcom(), metric.getMpc(), metric.getDac(),
        metric.getNom(), metric.getSize1(), metric.getSize2());

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

    private Map<String, Object> buildMap(){
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

    @Override
    public List<String> getKeys(){
        return new ArrayList<>(buildMap().keySet());
    }

    @Override
    public Map<String, Object> getValues(){
        return buildMap();
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

    public void incrementWmc(Integer increment){
        this.wmc += increment;
    }

    public void incrementRfc(Integer increment){
        this.rfc += increment;
    }

    public void incrementNoc(Integer increment){
        this.noc += increment;
    }

    public void incrementDit(Integer increment){
        this.dit += increment;
    }

    public void incrementCbo(Integer increment){
        this.cbo += increment;
    }

    public void incrementLcom(Integer increment){
        this.lcom += increment;
    }

    public void incrementMpc(Integer increment){
        this.mpc += increment;
    }

    public void incrementDac(Integer increment){
        this.dac += increment;
    }

    public void incrementNom(Integer increment){
        this.nom += increment;
    }

    public void incrementSize1(Integer increment){ this.size1 += increment; }

    public void incrementSize2(Integer increment){ this.size2 += increment; }
}
