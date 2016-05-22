package gitcrawler.crawler.local;

import com.messners.gitlab.api.GitLabApiException;
import gitcrawler.crawler.CommitCrawler;
import gitcrawler.crawler.Crawler;
import gitcrawler.crawler.FaultCrawler;
import gitcrawler.model.Commit;
import gitcrawler.model.Fault;
import gitcrawler.model.Issue;
import gitcrawler.model.Project;
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
    private final FaultCrawler faultCrawler;

    public LocalCrawler(Path gitPath) throws IOException {
        this.commitCrawler = new LocalCommitCrawler(gitPath);
        this.faultCrawler = new LocalFaultCrawler(getCommits());
    }

    public LocalCrawler(Project project) throws IOException, GitAPIException, GitLabApiException {
        this.commitCrawler = new LocalCommitCrawler(project.getLocalPath());
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
