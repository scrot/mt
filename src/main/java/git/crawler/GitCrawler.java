package git.crawler;

import com.messners.gitlab.api.GitLabApiException;
import git.model.*;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class GitCrawler {
    private final Map<Object, Commit> commits;
    private final Map<Integer, Issue> issues;
    private final Map<Path, List<Fault>> faults;
    //private final Map<Path, List<Commit>> changes;

    public GitCrawler(Project project) throws IOException, GitAPIException, GitLabApiException {
        System.out.println("Collecting commits...");
        CommitCrawler commitCrawler = new LocalCommitCrawler(project.getLocalPath());
        this.commits = commitCrawler.getCommits();
        System.out.println("Collecting issues...");
        this.issues = new GitlabIssueCrawler(project).getIssues();
        System.out.println("Collecting faults...");
        this.faults = new GitlabFaultCrawler(this.commits, this.issues).getFaults();
        //this.changes = collectChanges();

    }

    public Map<Object, Commit> getCommits() { return this.commits; }
    public Map<Integer, Issue> getIssues() { return this.issues; }
    public Map<Path, List<Fault>> getFaults() { return this.faults; }
    //public Map<Path, List<Commit>> getChanges() { return this.changes; }


    /*
    private Map<Path, List<Commit>> collectChanges(){
        Map<Path, List<Commit>> changes = new HashMap<>();
        for(Commit commit : this.getCommits().values()){
            List<Path> commitFiles = commit.getFiles();
            for(Path file : commitFiles){
                addValueToList(changes, file, commit);
            }
        }
        return changes;
    }


    private Map<Path, Set<Author>> collectAuthors(){
        Map<Path, Set<Author>> authors = new HashMap<>();
        Map<Path, List<Commit>> changes = this.getChanges();
        for(Map.Entry<Path, List<Commit>> entry : changes.entrySet()){
            Path path = entry.getKey();
            List<Commit> fileChanges = entry.getValue();
            Set<Author> fileAuthors = new HashSet<>();
            for(Commit fileChange : fileChanges){
                fileAuthors.add(fileChange.getAuthor());
            }
            authors.put(path, fileAuthors);
        }
        return authors;
    }
    */
}
