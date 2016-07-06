package org.uva.rdewildt.mt.gcrawler.git.crawler;

import org.eclipse.jgit.api.GarbageCollectCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.uva.rdewildt.mt.gcrawler.git.model.Author;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.uva.rdewildt.mt.gcrawler.git.GitUtils.gc;
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
                git.log().call().forEach(revCommit -> {
                    if(revCommit.getParentCount() > 0){
                        revCommits.add(revCommit);
                    }
                });
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
                AbstractTreeIterator oldTree = prepareTreeParser(git.getRepository(), revCommit);
                AbstractTreeIterator newTree = prepareTreeParser(git.getRepository(), revCommit.getParent(0));

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
            }
        } catch (GitAPIException | IOException e) {
        e.printStackTrace();
        }
        return files;
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader oldReader = repository.newObjectReader()) {
                treeParser.reset(oldReader, tree.getId());
            }

            return treeParser;
        }
    }
}
