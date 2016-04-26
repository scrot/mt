package git.crawler;

import git.model.Project;
import git.model.SimpleCommit;
import git.model.SimpleIssue;
import git.repository.GLRepoBuilder;

import java.util.List;

public abstract class GitCrawler {
    protected Project project;

    protected List<SimpleCommit> commits;
    protected List<SimpleIssue> issues;

    public GitCrawler(Project project) {
        GLRepoBuilder repoBuilder = new GLRepoBuilder(project);
    }
}
