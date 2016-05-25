package org.uva.rdewildt.mt.lims;

public class MetricCounter {
    private final String classname;

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

    public MetricCounter(String classname) {
        this.classname = classname;
        this.wmc = 0;
        this.noc = 0;
        this.rfc = 0;
        this.cbo = 0;
        this.dit = 0;
        this.lcom = 0;
        this.mpc = 0;
        this.dac = 0;
        this.nom = 0;
        this.size1 = 0;
        this.size2 = 0;
    }

    public Metric getMetric(){
        return new Metric(this.classname, this.wmc, this.noc, this.rfc, this.cbo,
                this.dit, this.lcom, this.mpc, this.dac, this.nom,
                this.size1, this.size2);
    }

    public void setMetric(Metric metric){
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
