package org.uva.rdewildt.mt.report.ghcrawler;

import org.uva.rdewildt.mt.fpms.git.model.Project;
import org.uva.rdewildt.mt.xloc.lang.Language;
import org.uva.rdewildt.mt.xloc.lang.Other;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/27/16.
 */
public class GhProject extends Project {
    private Language language;
    private String description;
    private int stars;
    private int forks;
    private int releases;
    private int subscribers;
    private int collaborators;

    public GhProject(){
        this(new Project(), new Other(), "", 0, 0, 0, 0, 0);
    }

    public GhProject(Project p, Language language, String description, Integer stars, Integer forks,
                     Integer releases, Integer subscribers, Integer collaborators) {
        super(p);
        this.language = language;
        this.description = description;
        this.stars = stars;
        this.forks = forks;
        this.releases = releases;
        this.subscribers = subscribers;
        this.collaborators = collaborators;
    }

    public GhProject(URL projectUrl, Path gitPath, Path binaryPath, String group, String project, String authToken,
                     Language language, String description, Integer stars, Integer forks, Integer releases,
                     Integer subscribers, Integer collaborators) {
        super(projectUrl, gitPath, binaryPath, group, project, authToken);
        this.language = language;
        this.description = description;
        this.stars = stars;
        this.forks = forks;
        this.releases = releases;
        this.subscribers = subscribers;
        this.collaborators = collaborators;

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
        Map<String, Object> projectMap = super.getValues();
        projectMap.put("Language", getLanguage());
        projectMap.put("Description", getDescription());
        projectMap.put("Stars", getStars());
        projectMap.put("Forks", getForks());
        projectMap.put("Releases", getForks());
        projectMap.put("Subscribers", getForks());
        projectMap.put("Collaborators", getForks());
        return projectMap;
    }

    public Language getLanguage() {
        return language;
    }

    public String getDescription() {
        return description;
    }

    public int getStars() {
        return stars;
    }

    public int getForks() {
        return forks;
    }

    public int getReleases() {
        return releases;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public int getCollaborators() {
        return collaborators;
    }
}
