package org.uva.rdewildt.mt.gcrawler.github;

import org.uva.rdewildt.mt.gcrawler.git.model.Project;
import org.uva.rdewildt.mt.utils.lang.Language;
import org.uva.rdewildt.mt.utils.lang.Other;

import java.nio.file.Path;
import java.util.*;

/**
 * Created by roy on 5/27/16.
 */
public class GhProject extends Project {
    public GhProject(){
        this(new Project(), new Other(), "", 0, 0, 0, null, false);
    }

    public GhProject(Project p, Language language, String description, Integer stars, Integer forks,
                     Integer size, Date pushdate, Boolean hasIssues) {
        super(p);
        this.map.putAll(new HashMap<String, Object>(){{
            put("Language", language);
            put("Description", description);
            put("Stars", stars);
            put("Forks", forks);
            put("Size", size);
            put("PushDate", pushdate);
            put("hasIssues", hasIssues);
        }});
    }

    public GhProject(String projectUrl, Path gitPath, Path binaryPath, String group, String project, Language language,
                     String description, Integer stars, Integer forks, Integer size, Date pushdate, Boolean hasIssues) {
        super(projectUrl, gitPath, binaryPath, group, project);
        this.map.putAll(new HashMap<String, Object>(){{
            put("Language", language);
            put("Description", description);
            put("Stars", stars);
            put("Forks", forks);
            put("Size", size);
            put("PushDate", pushdate);
            put("hasIssues", hasIssues);
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

    public Language getLanguage() {
        return (Language) this.map.get("Language");
    }

    public String getDescription() {
        return (String) this.map.get("Description");
    }

    public int getStars() {
        return (Integer) this.map.get("Stars");
    }

    public int getForks() {
        return (Integer) this.map.get("Forks");
    }

    public int getSize() {
        return (Integer) this.map.get("Size");
    }

    public Date getPushdate() {
        return (Date) this.map.get("PushDate");
    }

    public boolean getHasIssues() {
        return (Boolean) this.map.get("hasIssues");
    }
}
