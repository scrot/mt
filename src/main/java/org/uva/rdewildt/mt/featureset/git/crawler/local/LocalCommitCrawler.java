package org.uva.rdewildt.mt.featureset.git.crawler.local;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.uva.rdewildt.mt.featureset.git.crawler.CommitCrawler;
import org.uva.rdewildt.mt.featureset.git.model.Author;
import org.uva.rdewildt.mt.featureset.git.model.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by roy on 5/2/16.
 */
public class LocalCommitCrawler implements CommitCrawler {
    private final Git git;
    private Map<Object, Commit> commits;

    public LocalCommitCrawler(Git git) throws IOException {
        this.git = git;
    }

    @Override
    public Map<Object, Commit> getCommits() {
        if(this.commits == null){
            this.commits = collectCommits();
        }
        return this.commits;
    }

    public Map<RevCommit, List<Path>> getCommitsPaths() throws GitAPIException, IOException {
        Map<RevCommit, List<Path>> commitPaths = new HashMap<>();
        for(RevCommit commit : git.log().call()){
            commitPaths.put(commit, getCommitFiles(commit));
        }
        return commitPaths;
    }

    private Map<Object,Commit> collectCommits(){
        Map<Object, Commit> commits = new HashMap<>();
        try {
            for(RevCommit commit : git.log().call()){
                commits.put(commit.getId(), new Commit(
                        commit.getId(),
                        new Author(commit.getAuthorIdent().getName()),
                        commit.getFullMessage(),
                        commit.getAuthorIdent().getWhen(),
                        getCommitFiles(commit)));
            }
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return commits;
    }

    private List<Path> getCommitFiles(RevCommit commit) throws IOException, GitAPIException {
        List<Path> files = new ArrayList<>();

        if(commit.getParentCount() <= 0){
            return files;
        }

        ObjectReader reader = this.git.getRepository().newObjectReader();
        CanonicalTreeParser oldTree = new CanonicalTreeParser();
        oldTree.reset(reader, commit.getTree());
        CanonicalTreeParser newTree = new CanonicalTreeParser();
        newTree.reset(reader, commit.getParent(0).getTree());

        List<DiffEntry> diffs = git.diff().setNewTree(newTree).setOldTree(oldTree).call();
        for(DiffEntry diff : diffs){
            if (diff.getChangeType() == DiffEntry.ChangeType.DELETE){
                Path path = Paths.get(diff.getOldPath());
                files.add(path);
            }
            else {
                Path path = Paths.get(diff.getNewPath());
                files.add(path);
            }
        }

        return files;
    }


}
