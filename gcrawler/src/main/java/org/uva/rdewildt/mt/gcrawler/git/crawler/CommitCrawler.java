package org.uva.rdewildt.mt.gcrawler.git.crawler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.uva.rdewildt.mt.gcrawler.git.GitUtils;
import org.uva.rdewildt.mt.gcrawler.git.model.Author;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.uva.rdewildt.mt.gcrawler.git.GitUtils.gitFromPath;

/**
 * Created by roy on 5/26/16.
 */
public abstract class CommitCrawler {
    protected final Path gitRoot;

    public CommitCrawler(Path gitRoot) {
        this.gitRoot = gitRoot;
    }

    public abstract Map<String, Set<Commit>> getChanges();

    protected Map<RevCommit, List<Path>> getFilteredCommitsPaths(List<Path> includes) {
        Map<RevCommit, List<Path>> commitPaths = new HashMap<>();

        List<RevCommit> revCommits = new ArrayList<>();
        try (Git git =  gitFromPath(this.gitRoot)){
            if (git != null) {
                git.log().call().forEach(revCommits::add);
            }
        }
        catch (Exception e){
            e.getStackTrace();
        }


        revCommits.forEach(revCommit -> {
            List<Path> paths = getCommitPaths(revCommit, includes);
            if(!paths.isEmpty()){
                commitPaths.put(revCommit, paths);
            }
        });

        return commitPaths;
    }

    protected Commit revCommitToCommit(RevCommit revCommit){
        return new Commit(
                revCommit.getName(),
                new Author(revCommit.getAuthorIdent().getName()),
                revCommit.getFullMessage(),
                revCommit.getAuthorIdent().getWhen());
    }

    private List<Path> getCommitPaths(RevCommit revCommit, List<Path> includes) {
        List<Path> files = new ArrayList<>();

        try(Git git = gitFromPath(this.gitRoot)) {
            if (git != null) {
                try (ObjectReader reader = git.getRepository().newObjectReader()) {

                    CanonicalTreeParser oldTree = new CanonicalTreeParser();
                    oldTree.reset(reader, revCommit.getTree());
                    CanonicalTreeParser newTree = new CanonicalTreeParser();
                    newTree.reset(reader, revCommit.getParent(0).getTree());

                    List<DiffEntry> diffs = git.diff().setNewTree(newTree).setOldTree(oldTree).call();

                    diffs.forEach(diff -> {
                        if (diff.getChangeType() == DiffEntry.ChangeType.DELETE) {
                            Path path = Paths.get(diff.getOldPath());
                            if (includes.contains(path)) {
                                files.add(path);
                            }
                        } else {
                            Path path = Paths.get(diff.getNewPath());
                            if (includes.contains(path)) {
                                files.add(path);
                            }
                        }
                    });
                } catch (IOException | GitAPIException e) {
                    e.printStackTrace();
                }
            }
        }

        return files;
    }
}
