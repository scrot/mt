package report.model;

import java.util.Date;

public class GitData {
    // Git data
    private Date firstCommit;
    private Date lastCommit;
    private Integer releaseCount;
    private Integer issueCount;
    private Integer commitCount;

    public GitData(Date firstCommit, Date lastCommit, Integer releaseCount, Integer issueCount, Integer commitCount) {
        this.firstCommit = firstCommit;
        this.lastCommit = lastCommit;
        this.releaseCount = releaseCount;
        this.issueCount = issueCount;
        this.commitCount = commitCount;
    }

    public Date getFirstCommit() {
        return firstCommit;
    }

    public Date getLastCommit() {
        return lastCommit;
    }

    public Integer getReleaseCount() {
        return releaseCount;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public Integer getCommitCount() {
        return commitCount;
    }
}
