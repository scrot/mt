package org.uva.rdewildt.mt.featureset.git.model;

public class Fault {
    private final Issue issue;
    private final Commit commit;

    public Fault(Issue issue, Commit commit) {
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
