package git.crawler.local;

import com.messners.gitlab.api.GitLabApiException;
import git.crawler.CommitCrawler;
import git.crawler.Crawler;
import git.crawler.FaultCrawler;
import git.crawler.IssueCrawler;
import git.model.Commit;
import git.model.Fault;
import git.model.Issue;
import git.model.Project;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/5/16.
 */
public class LocalCrawler extends Crawler {
    private final CommitCrawler commitCrawler;
    private final IssueCrawler issueCrawler;
    private final FaultCrawler faultCrawler;

    public LocalCrawler(Project project) throws IOException, GitAPIException, GitLabApiException {
        this.commitCrawler = new LocalCommitCrawler(project.getLocalPath());
        this.issueCrawler = null;
        this.faultCrawler = new LocalFaultCrawler(getCommits());

    }

    @Override
    public Map<Object, Commit> getCommits() {
        return commitCrawler.getCommits();
    }

    @Override
    public Map<Integer, Issue> getIssues() {
        return new HashMap<>();
    }

    @Override
    public Map<Path, List<Fault>> getFaults() {
        return faultCrawler.getFaults();
    }
}
