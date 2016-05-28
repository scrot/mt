package org.uva.rdewildt.mt.gcrawler.git.model;

import org.uva.rdewildt.mt.lims.Reportable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Project implements Reportable {
    private final String projectUrl;
    private final Path gitPath;
    private final Path binaryPath;
    private final String group;
    private final String project;
    private final String authToken;

    public Project(){
        this(null, null, null, "", "", "");
    }

    public Project(Project p){
        this(p.getProjectUrl(), p.getGitRoot(), p.getBinaryRoot(), p.getGroup(), p.getProject(), p.getAuthToken());
    }

    public Project(String projectUrl, Path gitPath, Path binaryPath, String group, String project, String authToken) {
        this.gitPath = gitPath;
        this.binaryPath = binaryPath;
        this.projectUrl = projectUrl;
        this.group = group;
        this.project = project;
        this.authToken = authToken;
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(buildMap().keySet());
    }

    @Override
    public Map<String, Object> getValues() {
        return buildMap();
    }

    private Map<String, Object> buildMap(){
        return new LinkedHashMap<String, Object>(){{
            put("Name", getProject());
            put("Group", getGroup());
            put("URL", getProjectUrl());
            put("gitPath", getGitRoot());
            put("binaryPath", getBinaryRoot());
        }};
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public String getGroup() {
        return this.group;
    }

    public String getProject() {
        return this.project;
    }

    public Path getGitRoot() {
        return this.gitPath;
    }

    public Path getBinaryRoot() {
        return binaryPath;
    }

    public String getAuthToken() {
        return this.authToken;
    }
}
