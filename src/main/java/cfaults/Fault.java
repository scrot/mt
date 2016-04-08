package cfaults;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;

public class Fault {
    private final GHIssue issue;
    private final GHCommit commit;

    public Fault(GHIssue issue, GHCommit commit) {
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
