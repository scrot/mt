package org.uva.rdewildt.mt.fpms;

import org.uva.rdewildt.mt.lims.MetricCounter;

public class FeatureCounter extends MetricCounter {
    private int faults;
    private int changes;
    private int authors;
    private int age;

    public FeatureCounter(String classname) {
        super(classname);
        this.faults = 0;
        this.changes = 0;
        this.authors = 0;
        this.age = 0;
    }

    public Feature getFeature(){
        return new Feature(this.getMetric(), this.faults, this.changes, this.authors, this.age);
    }

    public void incrementFaults(Integer increment){
        this.faults += increment;
    }

    public void incrementChanges(Integer increment){
        this.changes += increment;
    }

    public void incrementAuthors(Integer increment){
        this.authors += increment;
    }

    public void incrementAge(Integer increment){
        this.age += increment;
    }

}
