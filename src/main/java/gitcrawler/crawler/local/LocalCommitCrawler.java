package gitcrawler.crawler.local;

import gitcrawler.crawler.CommitCrawler;
import gitcrawler.model.Author;
import gitcrawler.model.Commit;
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
    private final Path gitRoot;
    private Map<Object, Commit> commits;

    public LocalCommitCrawler(Path gitRoot) throws IOException {
        this.gitRoot = gitRoot;
        File gitFolder = Paths.get(gitRoot.toString(), ".git").toFile();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.setGitDir(gitFolder)
                .readEnvironment()
                .findGitDir()
                .build();
        this.git = new Git(repo);
    }

    @Override
    public Map<Object, Commit> getCommits() {
        if(this.commits == null){
            this.commits = collectCommits();
        }
        return this.commits;
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
                        getFiles(commit)));
            }
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
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
