package faults.model;

public class Fault {
    private final SimpleIssue issue;
    private final SimpleCommit commit;

    public Fault(SimpleIssue issue, SimpleCommit commit) {
        this.issue = issue;
        this.commit = commit;
    }

    public SimpleIssue getIssue() {
        return issue;
    }

    public SimpleCommit getCommit() {
        return commit;
    }
}
