package org.uva.rdewildt.mt.fpms.git.crawler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uva.rdewildt.mt.fpms.git.model.Commit;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by roy on 5/26/16.
 */
public class FCommitCrawler extends CommitCrawler {
    private final Map<String, Set<Commit>> commits;

    public FCommitCrawler(Git git, List<Path> includes) {
        super(git);
        this.commits = collectChanges(includes);
    }

    @Override
    public Map<String, Set<Commit>> getChanges() {
        return commits;
    }

    private Map<String, Set<Commit>> collectChanges(List<Path> includes) {
        Map<String, Set<Commit>> commits = new HashMap<>();
        Map<RevCommit, List<Path>> commitPaths =  getFilteredCommitsPaths(includes);
        for(Map.Entry<RevCommit, List<Path>> entry : commitPaths.entrySet()){
            entry.getValue().forEach(path -> {
                addValueToMapSet(commits, path.toString(), revCommitToCommit(entry.getKey()));
            });

        }
        return commits;
    }
}
