package metrics;

public class MetricCounter {
    private int wmc;
    private int noc;
    private int rfc;
    private int cbo;
    private int dit;
    private int lcom;

    public MetricCounter() {
        this.wmc = 0;
        this.noc = 0;
        this.rfc = 0;
        this.cbo = 0;
        this.dit = 0;
        this.lcom = 0;
    }

    public Metric getMetric() {
        return new Metric(wmc, noc, rfc, cbo, dit, lcom);
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
}
