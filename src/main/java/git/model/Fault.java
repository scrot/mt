package git.model;

public class Fault {
    private final SimpleIssue issue;
    private final Commit commit;

    public Fault(SimpleIssue issue, Commit commit) {
        this.issue = issue;
        this.commit = commit;
    }

    public SimpleIssue getIssue() {
        return issue;
    }

    public Commit getCommit() {
        return commit;
    }
}
