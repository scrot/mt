package gitcrawler.crawler.online;

import com.messners.gitlab.api.GitLabApiException;
import gitcrawler.crawler.CommitCrawler;
import gitcrawler.crawler.Crawler;
import gitcrawler.crawler.FaultCrawler;
import gitcrawler.crawler.IssueCrawler;
import gitcrawler.crawler.local.LocalCommitCrawler;
import gitcrawler.model.Commit;
import gitcrawler.model.Fault;
import gitcrawler.model.Issue;
import gitcrawler.model.Project;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
