package org.uva.rdewildt.mt.fpms;

import org.uva.rdewildt.mt.lims.Metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/22/16.
 */
public class Feature extends Metric {
    private int faults;
    private int changes;
    private int authors;
    private int age;

    public Feature(){
        this("");
    }

    public Feature(String classname) {
        super(classname);
        this.faults = 0;
        this.changes = 0;
        this.authors = 0;
        this.age = 0;
    }

    public Feature(Metric m, int faults, int changes, int authors, int age){
        super(m);
        this.faults = faults;
        this.changes = changes;
        this.authors = authors;
        this.age = age;
    }

    private Map<String, Object> buildMap(){
        Map<String, Object> features = super.getValues();
        features.put("Faults", getFaults());
        features.put("Changes", getChanges());
        features.put("Authors", getAuthors());
        features.put("Age", getAge());
        return features;
    }

    @Override
    public List<String> getKeys(){
        return new ArrayList<>(buildMap().keySet());
    }

    @Override
    public Map<String, Object> getValues(){
        return buildMap();
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
