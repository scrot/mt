package org.uva.rdewildt.mt.gcrawler.git.crawler;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.uva.rdewildt.mt.gcrawler.git.GitUtils.getFilteredCommitsPaths;
import static org.uva.rdewildt.mt.gcrawler.git.GitUtils.revCommitToCommit;
import static org.uva.rdewildt.mt.utils.MapUtils.addValueToMapSet;
import static org.uva.rdewildt.mt.utils.ReaderUtils.mixedCharsetFileReader;

/**
 * Created by roy on 5/26/16.
 */
public class FileCommitCrawler implements CommitCrawler {
    private Path gitRoot;
    private Map<String, Set<Commit>> fileCommits;

    public FileCommitCrawler(Path gitRoot, Boolean usePathNames, List<Path> includes) {
        this.gitRoot = gitRoot;
        try {
            this.fileCommits = collectChanges(includes, usePathNames);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Set<Commit>> getChanges() {
        return fileCommits;
    }

    private Map<String, Set<Commit>> collectChanges(List<Path> includes, Boolean usePathNames) throws IOException, GitAPIException {
        Map<String, Set<Commit>> commits = new HashMap<>();
        Map<RevCommit, List<Path>> commitPaths = getFilteredCommitsPaths(this.gitRoot, includes);
        if (usePathNames) {
            commitPaths.forEach(
                    (k, v) -> v.forEach(
                            path -> addValueToMapSet(commits, path.toString(), revCommitToCommit(k))));
        } else {
            commitPaths.forEach(
                    (k, v) -> v.forEach(
                            path -> addValueToMapSet(commits, extractPackageName(Paths.get(gitRoot.toString(), path.toString())), revCommitToCommit(k))));
        }
        return commits;
    }


    private String extractPackageName(Path path) {
        final String[] name = {path.toFile().getName().replaceFirst("[.][^.]+$", "")};
        List<String> lines = mixedCharsetFileReader(path, 50);
        lines.stream().forEach(l -> name[0] = l.contains("package") ? l.substring(8, l.length() - 1) + '.' + name[0] : name[0]);
        return name[0];
    }
}
