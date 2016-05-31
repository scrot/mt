package org.uva.rdewildt.mt.ovms;

import org.uva.rdewildt.mt.report.Reportable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/26/16.
 */
public class Overview implements Reportable {
    protected final Map<String, Object> map = new LinkedHashMap<>();

    public Overview(){
        this("");
    }

    public Overview(String projectname){
        this(projectname,0,0,0,0,0,0,0,0,0,0,0,0);
    }

    public Overview(Overview overview){
        this(overview.getProjectname(), overview.getFiles(), overview.getCloc(), overview.getSloc(), overview.getFaults(),
                overview.getChanges(), overview.getAuthors(), overview.getAge(), overview.getDev(), overview.getFdist(),
                overview.getCinf(), overview.getCgini(), overview.getFgini());
    }

    public Overview(String projectname, int files, int cloc, int sloc, int faults, int changes, int authors, int age,
                    int dev, int fdist, int cinf, double cgini, double fgini) {
        this.map.putAll(new LinkedHashMap<String, Object>(){{
            put("Project", projectname);
            put("Files", files);
            put("CLOC", cloc);
            put("SLOC", sloc);
            put("Faults", faults);
            put("Changes", changes);
            put("Authors", authors);
            put("Age", age);
            put("Dev", dev);
            put("FDist20", fdist);
            put("CinF20", cinf);
            put("FGini", fgini);
            put("CGini", cgini);
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

    public String getProjectname() {
        return (String) this.map.get("Project");
    }

    public int getFiles() {
        return (Integer) this.map.get("Files");
    }

    public int getCloc() {
        return (Integer) this.map.get("CLOC");
    }

    public int getSloc() {
        return (Integer) this.map.get("SLOC");
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

    public int getDev() {
        return (Integer) this.map.get("Dev");
    }

    public int getFdist() {
        return (Integer) this.map.get("FDist20");
    }

    public int getCinf() {
        return (Integer) this.map.get("CinF20");
    }

    public double getCgini() {
        return (Integer) this.map.get("FGini");
    }

    public double getFgini() {
        return (Integer) this.map.get("CGini");
    }
}
