package git.crawler.online;

import com.messners.gitlab.api.GitLabApiException;
import git.crawler.*;
import git.crawler.local.LocalCommitCrawler;
import git.model.*;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class OnlineCrawler extends Crawler {
    private final CommitCrawler commitCrawler;
    private final IssueCrawler issueCrawler;
    private final FaultCrawler faultCrawler;

    public OnlineCrawler(Project project) throws IOException, GitAPIException, GitLabApiException {
        this.commitCrawler = new LocalCommitCrawler(project.getLocalPath());
        this.issueCrawler = new GitlabIssueCrawler(project);
        this.faultCrawler = new GitlabFaultCrawler(getCommits(), getIssues());

    }

    @Override
    public Map<Object, Commit> getCommits() { return this.commitCrawler.getCommits(); }

    @Override
    public Map<Integer, Issue> getIssues() { return this.issueCrawler.getIssues(); }

    @Override
    public Map<Path, List<Fault>> getFaults() { return this.faultCrawler.getFaults(); }
}
