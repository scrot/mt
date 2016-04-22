package faults.model;
import com.messners.gitlab.api.models.Issue;
import org.kohsuke.github.GHIssue;

import java.io.IOException;

public class SimpleIssue {
    private final Integer id;
    private final String title;
    private final String description;
    private final String state;

    public SimpleIssue(Issue glIssue) {
        this.id = glIssue.getIid();
        this.title = glIssue.getTitle();
        this.description = glIssue.getDescription();
        this.state = glIssue.getState();
    }

    public SimpleIssue(GHIssue ghIssue) throws IOException {
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
