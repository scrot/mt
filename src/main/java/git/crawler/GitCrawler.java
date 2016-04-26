package git.crawler;

import git.model.Project;
import git.model.Commit;
import git.model.SimpleIssue;

import java.util.Map;

public abstract class GitCrawler {
    protected Project project;

    protected Map<String, Commit> commits;
    protected Map<Integer, SimpleIssue> issues;


    public GitCrawler(Project project) {
    }

    public Map<String, Commit> getCommits() {
        return commits;
    }

    public Map<Integer, SimpleIssue> getIssues() {
        return issues;
    }
}
