package faults.fault;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;

public class GHFault {
    private final GHIssue issue;
    private final GHCommit commit;

    public GHFault(GHIssue issue, GHCommit commit) {
        this.issue = issue;
        this.commit = commit;
    }

    public GHIssue getIssue() {
        return issue;
    }

    public GHCommit getCommit() {
        return commit;
    }
}
