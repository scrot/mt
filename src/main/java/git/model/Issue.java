package git.model;
import org.kohsuke.github.GHIssue;

import java.io.IOException;

public class Issue {
    private final Integer id;
    private final String title;
    private final String description;
    private final String state;

    public Issue(com.messners.gitlab.api.models.Issue glIssue) {
        this.id = glIssue.getIid();
        this.title = glIssue.getTitle();
        this.description = glIssue.getDescription();
        this.state = glIssue.getState();
    }

    public Issue(GHIssue ghIssue) throws IOException {
        this.id = ghIssue.getId();
        this.title = ghIssue.getTitle();
        this.description = ghIssue.getBody();
        this.state = ghIssue.getState().name();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }
}
