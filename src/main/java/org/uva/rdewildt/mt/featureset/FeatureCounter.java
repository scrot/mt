package org.uva.rdewildt.mt.featureset;

import lims.MetricCounter;

/**
 * Created by roy on 5/22/16.
 */
public class FeatureCounter extends MetricCounter {
    private int faults;
    private int changes;
    private int authors;
    private int age;

    public FeatureCounter() {
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
