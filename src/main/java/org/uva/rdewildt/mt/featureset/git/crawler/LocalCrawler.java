package org.uva.rdewildt.mt.featureset.git.crawler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.uva.rdewildt.mt.featureset.git.model.Commit;
import org.uva.rdewildt.mt.featureset.git.model.Fault;
import org.uva.rdewildt.mt.featureset.git.model.Issue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by roy on 5/5/16.
 */
public class LocalCrawler extends Crawler {
    private Git git;
    private final CommitCrawler commitCrawler;
    private final FaultCrawler faultCrawler;

    public LocalCrawler(Path gitRoot) throws Exception {
        this.git = getGitFromFileSystem(gitRoot);
        this.commitCrawler = new LocalCommitCrawler(git);
        this.faultCrawler = new LocalFaultCrawler(getCommits());
    }

    @Override
    public Map<String, Set<Commit>> getCommits() {
        return commitCrawler.getCommits();
    }

    @Override
    public Map<Integer, Issue> getIssues() {
        return new HashMap<>();
    }

    @Override
    public Map<String, Set<Fault>> getFaults() {
        return faultCrawler.getFaults();
    }

    private Git getGitFromFileSystem(Path gitPath) throws IOException {
        File gitFolder = Paths.get(gitPath.toString(), ".git").toFile();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.setGitDir(gitFolder)
                .readEnvironment()
                .findGitDir()
                .build();
        return new Git(repo);
    }
}
