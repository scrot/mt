package org.uva.rdewildt.mt.featureset.git.crawler.local;

import com.messners.gitlab.api.GitLabApiException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.uva.rdewildt.mt.featureset.git.crawler.CommitCrawler;
import org.uva.rdewildt.mt.featureset.git.crawler.Crawler;
import org.uva.rdewildt.mt.featureset.git.crawler.FaultCrawler;
import org.uva.rdewildt.mt.featureset.git.model.Commit;
import org.uva.rdewildt.mt.featureset.git.model.Fault;
import org.uva.rdewildt.mt.featureset.git.model.Issue;
import org.uva.rdewildt.mt.featureset.git.model.Project;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/5/16.
 */
public class LocalCrawler extends Crawler {
    private Git git;
    private final CommitCrawler commitCrawler;
    private final FaultCrawler faultCrawler;

    public LocalCrawler(Path gitPath) throws IOException {
        this.git = getGitFromPath(gitPath);
        this.commitCrawler = new LocalCommitCrawler(git);
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

    private Git getGitFromPath(Path gitPath) throws IOException {
        File gitFolder = Paths.get(gitPath.toString(), ".git").toFile();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.setGitDir(gitFolder)
                .readEnvironment()
                .findGitDir()
                .build();
        return new Git(repo);
    }
}
