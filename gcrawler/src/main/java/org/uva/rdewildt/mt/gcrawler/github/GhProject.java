package org.uva.rdewildt.mt.gcrawler.github;

import org.uva.rdewildt.mt.gcrawler.git.model.Project;
import org.uva.rdewildt.mt.utils.lang.Language;
import org.uva.rdewildt.mt.utils.lang.Other;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/27/16.
 */
public class GhProject extends Project {
    private final Language language;
    private final String description;
    private final int stars;
    private final int forks;
    private final int size;
    private final Date pushdate;

    public GhProject(){
        this(new Project(), new Other(), "", 0, 0, 0, null);
    }

    public GhProject(Project p, Language language, String description, Integer stars, Integer forks,
                     Integer size, Date pushdate) {
        super(p);
        this.language = language;
        this.description = description;
        this.stars = stars;
        this.forks = forks;
        this.size = size;
        this.pushdate = pushdate;
    }

    public GhProject(String projectUrl, Path gitPath, Path binaryPath, String group, String project, String authToken,
                     Language language, String description, Integer stars, Integer forks, Integer size, Date pushdate) {
        super(projectUrl, gitPath, binaryPath, group, project, authToken);
        this.language = language;
        this.description = description;
        this.stars = stars;
        this.forks = forks;
        this.size = size;
        this.pushdate = pushdate;

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
        projectMap.put("Size", getSize());
        projectMap.put("PushDate", getPushdate());
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

    public int getSize() {
        return size;
    }

    public Date getPushdate() {
        return pushdate;
    }
}
