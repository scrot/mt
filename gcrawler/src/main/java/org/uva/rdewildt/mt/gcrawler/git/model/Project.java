package org.uva.rdewildt.mt.gcrawler.git.model;

import org.eclipse.jgit.transport.URIish;
import org.uva.rdewildt.mt.gcrawler.github.GhProject;
import org.uva.rdewildt.mt.report.Reportable;

import java.nio.file.Path;
import java.util.*;

public class Project implements Reportable {
    protected final Map<String, Object> map = new LinkedHashMap<>();

    public Project(){
        this(null, null, null, "", "");
    }

    public Project(Project p){
        this(p.getProjectUrl(), p.getGitRoot(), p.getBinaryRoot(), p.getGroup(), p.getProject());
    }

    public Project(String projectUrl, Path gitPath, Path binaryPath, String group, String project) {
        this.map.putAll(new HashMap<String, Object>(){{
            put("Name", project);
            put("Group", group);
            put("URL", projectUrl);
            put("GitPath", gitPath);
            put("BinaryPath", binaryPath);
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

    public String getId() { return this.getGroup() + '-' + this.getProject(); }

    public String getProject() {
        return (String) this.map.get("Name");
    }

    public String getGroup() {
        return (String) this.map.get("Group");
    }

    public String getProjectUrl() {
        return (String) this.map.get("URL");
    }

    public Path getGitRoot() {
        return (Path) this.map.get("GitPath");
    }

    public Path getBinaryRoot() {
        return (Path) this.map.get("BinaryPath");
    }

    public void setProject(String value) {
        this.map.put("Name", value);
    }

    public void setGroup(String value) {
        this.map.put("Group", value);
    }

    public void setProjectUrl(String value) {
        this.map.put("URL", value);
    }

    public void setGitRoot(String value) {
        this.map.put("GitPath", value);
    }

    public void setBinaryRoot(String value) {
        this.map.put("BinaryPath", value);
    }

    @Override
    public int hashCode() {
        return this.getProjectUrl().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Project){
            return Objects.equals(((Project) o).getProjectUrl(), this.getProjectUrl());
        }
        return super.equals(o);
    }
}
