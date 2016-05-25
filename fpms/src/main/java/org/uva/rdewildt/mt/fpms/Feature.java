package org.uva.rdewildt.mt.fpms;

import org.uva.rdewildt.mt.lims.Metric;

import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/22/16.
 */
public class Feature extends Metric {
    private final int faults;
    private final int changes;
    private final int authors;
    private final int age;

    public Feature(Feature f){
        super(f);
        this.faults = f.getFaults();
        this.changes = f.getChanges();
        this.authors = f.getAuthors();
        this.age = f.getAge();

    }

    public Feature(Metric m, int faults, int changes, int authors, int age){
        super(m);
        this.faults = faults;
        this.changes = changes;
        this.authors = authors;
        this.age = age;
    }

    public Feature(String classname, int wmc, int noc, int rfc, int cbo, int dit,
                   int lcom, int mpc, int dac, int nom, int size1,
                   int size2, int faults, int changes, int authors, int age) {
        super(classname, wmc, noc, rfc, cbo, dit, lcom, mpc, dac, nom, size1, size2);
        this.faults = faults;
        this.changes = changes;
        this.authors = authors;
        this.age = age;
    }

    public static List<String> getFeatureNames(){
        List<String> featureNames = Metric.getMetricNames();
        featureNames.add("Faults");
        featureNames.add("Changes");
        featureNames.add("Authors");
        featureNames.add("Age");
        return featureNames;
    }

    public Map<String, Object> getFeatures(){
        Map<String, Object> features = getMetrics();
        features.put("Faults", getFaults());
        features.put("Changes", getChanges());
        features.put("Authors", getAuthors());
        features.put("Age", getAge());
        return features;
    }

    public int getFaults() {
        return faults;
    }

    public int getChanges() {
        return changes;
    }

    public int getAuthors() {
        return authors;
    }

    public int getAge() {
        return age;
    }
}
