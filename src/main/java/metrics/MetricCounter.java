package metrics;

public class MetricCounter {
    private int wmc;
    private int noc;
    private int rfc;
    private int cbo;
    private int dit;
    private int lcom;
    private int npm;
    private int ca;

    public MetricCounter() {
        this.wmc = 0;
        this.noc = 0;
        this.rfc = 0;
        this.cbo = 0;
        this.dit = 0;
        this.lcom = 0;
        this.npm = 0;
        this.ca = 0;
    }

    public Metric getMetric() {
        return new Metric(wmc, noc, rfc, cbo, dit, lcom, npm, ca);
    }

    public void incrementNoc(){
        this.noc++;
    }

    public void incrementDit(){
        this.dit++;
    }
    /*
    public Metric add(Object object){
        if(object instanceof Metric){
            Metric y = (Metric) object;
            Metric total = new Metric();
            total.setWmc(this.getWmc() + y.getWmc());
            total.setDit(this.getDit() + y.getDit());
            total.setNoc(this.getNoc() + y.getNoc());
            total.setCbo(this.getCbo() + y.getCbo());
            total.setRfc(this.getRfc() + y.getRfc());
            total.setLcom(this.getLcom() + y.getLcom());
            total.addAfferentCoupling(this.getAfferentCouplingClasses(), y.getAfferentCouplingClasses());
            total.setNpm(this.getNpm() + y.getNpm());
            return total;
        }
        throw new InvalidParameterException();
    }
    */
}
