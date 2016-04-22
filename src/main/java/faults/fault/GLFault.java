package faults.fault;

import com.messners.gitlab.api.models.Commit;
import com.messners.gitlab.api.models.Issue;

public class GLFault {
    private final Issue issue;
    private final Commit commit;

    public GLFault(Issue issue, Commit commit) {
        this.issue = issue;
        this.commit = commit;
    }

    public Issue getIssue() {
        return issue;
    }

    public Commit getCommit() {
        return commit;
    }
}
