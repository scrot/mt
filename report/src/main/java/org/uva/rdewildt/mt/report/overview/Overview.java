package org.uva.rdewildt.mt.report.overview;

import org.uva.rdewildt.mt.lims.Reportable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/26/16.
 */
public class Overview implements Reportable{
    private String projectname;

    private int totalfiles;
    private int filterfiles;

    private int cloc;
    private int sloc;

    private int faults;
    private int changes;
    private int authors;

    private int age;
    private int dev;

    private int fdist;
    private int cinf;
    private int cgini;
    private int fgini;

    public Overview(){
        this("");
    }

    public Overview(String classname){
        this(classname,0,0,0,0,0,0,0,0,0,0,0,0,0);
    }

    public Overview(Overview overview){
        this(overview.getProjectname(), overview.getTotalfiles(),
        overview.getFilterfiles(), overview.getCloc(),
        overview.getSloc(), overview.getFaults(),
        overview.getChanges(), overview.getAuthors(),
        overview.getAge(), overview.getDev(),
        overview.getFdist(), overview.getCinf(),
        overview.getCgini(), overview.getFgini());
    }

    public Overview(String projectname, int totalfiles, int filterfiles, int cloc, int sloc,
                    int faults, int changes, int authors, int age, int dev, int fdist, int cinf,
                    int cgini, int fgini) {
        this.projectname = projectname;
        this.totalfiles = totalfiles;
        this.filterfiles = filterfiles;
        this.cloc = cloc;
        this.sloc = sloc;
        this.faults = faults;
        this.changes = changes;
        this.authors = authors;
        this.age = age;
        this.dev = dev;
        this.fdist = fdist;
        this.cinf = cinf;
        this.cgini = cgini;
        this.fgini = fgini;
    }

    private Map<String, Object> buildMap(){
        return new HashMap<String, Object>(){{
            put("ProjectName", getProjectname());
            put("TotalFiles", getTotalfiles());
            put("FilteredFiles", getFilterfiles());
            put("CLOC", getCloc());
            put("SLOC", getSloc());
            put("Faults", getFaults());
            put("Changes", getChanges());
            put("Authors", getAuthors());
            put("Age", getAge());
            put("Dev", getDev());
            put("FDist", getFdist());
            put("CinF", getCinf());
            put("FGini", getFgini());
            put("CGini", getCgini());
        }};
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(buildMap().keySet());
    }

    @Override
    public Map<String, Object> getValues() {
        return buildMap();
    }

    public String getProjectname() {
        return projectname;
    }

    public int getTotalfiles() {
        return totalfiles;
    }

    public int getFilterfiles() {
        return filterfiles;
    }

    public int getCloc() {
        return cloc;
    }

    public int getSloc() {
        return sloc;
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

    public int getDev() {
        return dev;
    }

    public int getFdist() {
        return fdist;
    }

    public int getCinf() {
        return cinf;
    }

    public int getCgini() {
        return cgini;
    }

    public int getFgini() {
        return fgini;
    }


}
