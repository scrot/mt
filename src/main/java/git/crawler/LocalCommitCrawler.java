package git.crawler;

import git.model.Author;
import git.model.Commit;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/2/16.
 */
public class LocalCommitCrawler implements CommitCrawler {
    private final Git git;
    private final Map<Object, Commit> commits;

    public LocalCommitCrawler(Path gitProjectRoot) throws IOException, GitAPIException {
        File gitRoot = Paths.get(gitProjectRoot.toString(), ".git").toFile();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.setGitDir(gitRoot)
                .readEnvironment()
                .findGitDir()
                .build();
        this.git = new Git(repo);
        this.commits = collectCommits();
    }

    @Override
    public Map<Object, Commit> getCommits() {
        return this.commits;
    }

    private Map<Object,Commit> collectCommits() throws IOException, GitAPIException {
        Map<Object, Commit> commits = new HashMap<>();
        for(RevCommit commit : git.log().call()){
            commits.put(commit.getId(), new Commit(
                    commit.getId(),
                    new Author(commit.getAuthorIdent().getName()),
                    commit.getFullMessage(),
                    commit.getAuthorIdent().getWhen(),
                    getFiles(commit)));
        }
        return commits;
    }

    private List<Path> getFiles(RevCommit commit) throws IOException, GitAPIException {
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
                files.add(Paths.get(diff.getOldPath()));
            }
            else {
                files.add(Paths.get(diff.getNewPath()));
            }
        }

        return files;
    }


}
