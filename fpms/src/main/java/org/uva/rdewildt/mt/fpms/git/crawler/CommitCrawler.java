package org.uva.rdewildt.mt.fpms.git.crawler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.uva.rdewildt.mt.fpms.git.model.Author;
import org.uva.rdewildt.mt.fpms.git.model.Commit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by roy on 5/26/16.
 */
public abstract class CommitCrawler {
    protected final Git git;

    public CommitCrawler(Git git) {
        this.git = git;
    }

    public abstract Map<String, Set<Commit>> getChanges();

    protected Map<RevCommit, List<Path>> getFilteredCommitsPaths(List<Path> includes) {
        Map<RevCommit, List<Path>> commitPaths = new HashMap<>();
        try {
            git.log().call().forEach(commit -> {
                List<Path> paths = getCommitPaths(commit, includes);
                if(!paths.isEmpty()){
                    commitPaths.put(commit, paths);
                }
            });
        }
        catch (Exception e){
            e.getStackTrace();
        }
        return commitPaths;
    }

    protected Commit revCommitToCommit(RevCommit revCommit){
        return new Commit(
                revCommit.getId(),
                new Author(revCommit.getAuthorIdent().getName()),
                revCommit.getFullMessage(),
                revCommit.getAuthorIdent().getWhen());
    }

    private List<Path> getCommitPaths(RevCommit revCommit, List<Path> includes) {
        List<Path> files = new ArrayList<>();

        try {
            ObjectReader reader = this.git.getRepository().newObjectReader();
            CanonicalTreeParser oldTree = new CanonicalTreeParser();
            oldTree.reset(reader, revCommit.getTree());
            CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(reader, revCommit.getParent(0).getTree());

            List<DiffEntry> diffs = git.diff().setNewTree(newTree).setOldTree(oldTree).call();

            diffs.forEach(diff -> {
                if (diff.getChangeType() == DiffEntry.ChangeType.DELETE){
                    Path path = Paths.get(diff.getOldPath());
                    if(includes.contains(path)){
                        files.add(path);
                    }
                }
                else {
                    Path path = Paths.get(diff.getNewPath());
                    if(includes.contains(path)){
                        files.add(path);
                    }
                }
            });
        }
        catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }

        return files;
    }

    protected static <K, V> void addValueToMapSet(Map<K, Set<V>> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new HashSet<V>() {{ add(value); }});
        }
        else {
            Set<V> newvalue = map.get(key);
            newvalue.add(value);
            map.put(key, newvalue);
        }
    }
}
