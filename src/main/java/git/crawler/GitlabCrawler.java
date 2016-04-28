package git.crawler;

import com.messners.gitlab.api.GitLabApiException;
import com.messners.gitlab.api.models.*;
import com.messners.gitlab.api.models.Project;
import git.api.GitlabAPI;
import git.model.*;
import git.model.Author;
import git.model.Commit;
import git.model.Issue;
import git.repository.GLRepoBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class GitlabCrawler implements GitCrawler {
    private final GitlabAPI gitlab;
    private final Integer projectID;
    private final com.messners.gitlab.api.models.Project glProject;

    private Map<String, Commit> commits;
    private Map<Integer, Issue> issues;
    private Map<Path, List<Fault>> faults;
    private Map<Path, List<Commit>> changes;
    private Map<Path, Set<Author>> authors;

    public GitlabCrawler(GitlabProject project) throws GitLabApiException {
        GLRepoBuilder repoBuilder = new GLRepoBuilder(project);
        this.gitlab = repoBuilder.getGitlabApi();
        this.projectID = repoBuilder.getProjectID();
        this.glProject = this.gitlab.getProjectApi().getProject(this.projectID);
    }

    @Override
    public Map<String, Commit> getCommits() {
        if(this.commits == null){
            try {
                this.commits = collectCommits();
            } catch (GitLabApiException e) {
                e.printStackTrace();
            }
        }
        return this.commits;
    }

    @Override
    public Map<Integer, Issue> getIssues() {
        if(this.issues == null){
            try {
                this.issues = collectIssues();
            } catch (GitLabApiException e) {
                e.printStackTrace();
            }
        }
        return this.issues;
    }

    @Override
    public Map<Path, List<Fault>> getFaults() {
        if(this.faults == null){
            this.faults = collectFaults();
        }
        return this.faults;
    }

    @Override
    public Map<Path, List<Commit>> getChanges() {
        if(this.changes == null){
            this.changes = collectChanges();
        }
        return this.changes;
    }

    @Override
    public Map<Path, Set<Author>> getAuthors() {
        if(this.authors == null){
            this.authors = collectAuthors();
        }
        return this.authors;
    }

    @Override
    public Date createdAt() {
        return this.glProject.getCreatedAt();
    }

    @Override
    public Date lastModified() {
        return this.glProject.getLastActivityAt();
    }

    private Map<String, Commit> collectCommits() throws GitLabApiException {
        System.out.println("Collecting commits...");
        List<com.messners.gitlab.api.models.Commit> glCommits = this.gitlab.getCommitsAPI().getCommits(this.projectID);
        Map<String, Commit> commits = new HashMap<>();
        for (com.messners.gitlab.api.models.Commit glCommit : glCommits){
            commits.put(glCommit.getId(), new Commit(
                    glCommit.getId(),
                    new Author(glCommit.getAuthor().getName()),
                    glCommit.getMessage(),
                    glCommit.getTimestamp(),
                    getFiles(glCommit),
                    getFaultPattern()));
        }
        return commits;
    }

    private Map<Integer, Issue> collectIssues() throws GitLabApiException {
        System.out.println("Collecting issues...");
        Map<Integer, Issue> issueMap = new HashMap<>();
        List<com.messners.gitlab.api.models.Issue> issueList = this.gitlab.getIssuesAPI().getIssues(this.projectID);
        for(com.messners.gitlab.api.models.Issue issue : issueList){
            issueMap.put(issue.getIid(), new Issue(issue));
        }
        return issueMap;
    }

    private Map<Path, List<Fault>> collectFaults(){
        System.out.println("Collecting faults...");
        Map<Path, List<Fault>> classFaults = new HashMap<>();
        for (Map.Entry<Commit, List<Issue>> commit : buildCommitIssueMap().entrySet()) {
            List<Path> files = commit.getKey().getFiles();
            List<Issue> issues = commit.getValue();

            for (Path file : files){
                for(Issue issue : issues){
                    addValueToList(classFaults, file, new Fault(issue,commit.getKey()));
                }
            }
        }
        return classFaults;
    }

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

    private List<Path> getFiles(com.messners.gitlab.api.models.Commit commit) throws GitLabApiException {
        List<Path> files = new ArrayList<>();
        List<Diff> commitDiffs = this.gitlab.getCommitsAPI().getDiffs(this.projectID, commit.getId());
        for(Diff diff : commitDiffs){
            files.add(Paths.get(diff.getNewPath()));
        }
        return files;
    }

    private Map<Commit, List<Issue>> buildCommitIssueMap() {
        Map<Commit, List<Issue>> commitIssueMap = new HashMap<>();

        for(Commit commit : this.getCommits().values()){
            for(Integer issueNumber : commit.getIssueNumbers()){
                if(this.getIssues().containsKey(issueNumber)) {
                    Issue issue = this.getIssues().get(issueNumber);
                    addValueToList(commitIssueMap, commit, issue);
                }
            }
        }
        return commitIssueMap;
    }

    private <K, V> void addValueToList(Map<K, List<V>> map, K key, V value){
        if(!map.containsKey(key)){
            map.put(key, new ArrayList<V>(){{add(value);}});
        }

        else {
            List<V> currentIssues = map.get(key);
            currentIssues.add(value);
            map.put(key, currentIssues);
        }
    }

    private Pattern getFaultPattern(){
        return Pattern.compile(
                "((?:[Cc]los(?:e[sd]?|ing)|[Ff]ix(?:e[sd]|ing)?) +(?:(?:issues? +)?#\\d+(?:(?:, *| +and +)?))+)"
        );
    }
}
