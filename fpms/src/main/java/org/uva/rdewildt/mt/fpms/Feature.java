package org.uva.rdewildt.mt.fpms;

import org.uva.rdewildt.mt.bcms.Metric;
import org.uva.rdewildt.mt.utils.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/22/16.
 */
public class Feature extends Metric {

    public Feature() {
        this("");
    }

    public Feature(String classname) {
        super(classname);
        this.map.putAll(new HashMap<String, Object>() {{
            put("Faults", 0);
            put("Changes", 0);
            put("Authors", 0);
            put("Age", 0);
        }});
    }

    public Feature(Metric m, int faults, int changes, int authors, int age) throws NoSuchFieldException {
        this.setValues(m.getValues());
        this.map.putAll(new HashMap<String, Object>() {{
            put("Faults", faults);
            put("Changes", changes);
            put("Authors", authors);
            put("Age", age);
        }});
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(this.map.keySet());
    }

    @Override
    public Map<String, Object> getValues() {
        return this.map;
    }

    @Override
    public Feature getNewInstance() {
        return new Feature();
    }

    public int getFaults() {
        return (Integer) this.map.get("Faults");
    }

    public int getChanges() {
        return (Integer) this.map.get("Changes");
    }

    public int getAuthors() {
        return (Integer) this.map.get("Authors");
    }

    public int getAge() {
        return (Integer) this.map.get("Age");
    }

    public void incrementFaults(Integer increment) {
        MapUtils.incrementMapValue(this.map, "Faults", increment);
    }

    public void incrementChanges(Integer increment) {
        MapUtils.incrementMapValue(this.map, "Changes", increment);
    }

    public void incrementAuthors(Integer increment) {
        MapUtils.incrementMapValue(this.map, "Authors", increment);
    }

    public void incrementAge(Integer increment) {
        MapUtils.incrementMapValue(this.map, "Age", increment);
    }
}
