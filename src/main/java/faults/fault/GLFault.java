package faults.fault;

import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabIssue;

public class GLFault {
    private final GitlabIssue issue;
    private final GitlabCommit commit;

    public GLFault(GitlabIssue issue, GitlabCommit commit) {
        this.issue = issue;
        this.commit = commit;
    }

    public GitlabIssue getIssue() {
        return issue;
    }

    public GitlabCommit getCommit() {
        return commit;
    }
}
