package lims;

public class MetricCounter {
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

    public MetricCounter() {
        this.wmc = 0;
        this.noc = 0;
        this.rfc = 0;
        this.cbo = 0;
        this.dit = 0;
        this.lcom = 0;
        this.mpc = 0;
        this.dac = 0;
        this.nom = 0;
        this.size2 = 0;
    }

    public int getWmc() {
        return wmc;
    }

    public int getNoc() {
        return noc;
    }

    public int getRfc() {
        return rfc;
    }

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
