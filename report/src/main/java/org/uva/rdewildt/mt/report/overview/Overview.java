package org.uva.rdewildt.mt.report.overview;

import org.uva.rdewildt.mt.lims.Reportable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/26/16.
 */
public class Overview implements Reportable{
    private final String projectname;

    private int files;
    private int cloc;
    private int sloc;

    private int faults;
    private int changes;
    private int authors;

    private int age;
    private int dev;

    private int fdist;
    private int cinf;
    private double cgini;
    private double fgini;

    public Overview(){
        this("");
    }

    public Overview(String projectname){
        this(projectname,0,0,0,0,0,0,0,0,0,0,0,0);
    }

    public Overview(Overview overview){
        this(overview.getProjectname(),
        overview.getFiles(), overview.getCloc(),
        overview.getSloc(), overview.getFaults(),
        overview.getChanges(), overview.getAuthors(),
        overview.getAge(), overview.getDev(),
        overview.getFdist(), overview.getCinf(),
        overview.getCgini(), overview.getFgini());
    }

    public Overview(String projectname, int files, int cloc, int sloc,
                    int faults, int changes, int authors, int age, int dev, int fdist, int cinf,
                    double cgini, double fgini) {
        this.projectname = projectname;
        this.files = files;
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
        return new LinkedHashMap<String, Object>(){{
            put("ProjectName", getProjectname());
            put("Files", getFiles());
            put("CLOC", getCloc());
            put("SLOC", getSloc());
            put("Faults", getFaults());
            put("Changes", getChanges());
            put("Authors", getAuthors());
            put("Age", getAge());
            put("Dev", getDev());
            put("FDist20", getFdist());
            put("CinF20", getCinf());
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

    public int getFiles() {
        return files;
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

    public double getCgini() {
        return cgini;
    }

    public double getFgini() {
        return fgini;
    }

    public void setFiles(int files) {
        this.files = files;
    }

    public void setCloc(int cloc) {
        this.cloc = cloc;
    }

    public void setSloc(int sloc) {
        this.sloc = sloc;
    }

    public void setFaults(int faults) {
        this.faults = faults;
    }

    public void setChanges(int changes) {
        this.changes = changes;
    }

    public void setAuthors(int authors) {
        this.authors = authors;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDev(int dev) {
        this.dev = dev;
    }

    public void setFdist(int fdist) {
        this.fdist = fdist;
    }

    public void setCinF(int cinf) {
        this.cinf = cinf;
    }

    public void setCgini(double cgini) {
        this.cgini = cgini;
    }

    public void setFgini(double fgini) {
        this.fgini = fgini;
    }
}
