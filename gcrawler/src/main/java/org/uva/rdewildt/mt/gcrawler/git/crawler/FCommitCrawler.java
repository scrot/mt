package org.uva.rdewildt.mt.gcrawler.git.crawler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by roy on 5/26/16.
 */
public class FCommitCrawler extends CommitCrawler {
    Path gitRoot;
    private final Map<String, Set<Commit>> commits;

    public FCommitCrawler(Git git, Path gitRoot, List<Path> includes) {
        super(git);
        this.gitRoot = gitRoot;
        this.commits = collectChanges(includes);
    }

    @Override
    public Map<String, Set<Commit>> getChanges() {
        return commits;
    }

    private Map<String, Set<Commit>> collectChanges(List<Path> includes) {
        Map<String, Set<Commit>> commits = new HashMap<>();
        Map<RevCommit, List<Path>> commitPaths = getFilteredCommitsPaths(includes);
        commitPaths.forEach((k,v) -> v.forEach(path -> addValueToMapSet(commits, getFileName(Paths.get(gitRoot.toString(), path.toString())), revCommitToCommit(k))));
        return commits;
    }

    private String getFileName(Path path) {
        final String[] name = {path.toFile().getName().replaceFirst("[.][^.]+$", "")};
        try {
            Files.lines(path).limit(5).forEach(l -> name[0] = l.contains("package") ? l.substring(8, l.length() - 1) + '.' + name[0] : name[0]);
        } catch (IOException ignored) { }
        return name[0];
    }
}
