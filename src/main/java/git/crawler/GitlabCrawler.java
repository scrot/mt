package git.crawler;

import com.messners.gitlab.api.GitLabApiException;
import com.messners.gitlab.api.models.Diff;
import git.api.GitlabAPI;
import git.model.Project;
import git.model.Commit;
import git.model.GitlabCommit;
import git.model.Issue;
import git.repository.GLRepoBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitlabCrawler extends GitCrawler {
    private final GitlabAPI gitlab;
    private final Integer projectID;

    public GitlabCrawler(Project project) throws GitLabApiException {
        GLRepoBuilder repoBuilder = new GLRepoBuilder(project);
        this.gitlab = repoBuilder.getGitlabApi();
        this.projectID = repoBuilder.getProjectID();
        this.commits = collectCommits();
        this.issues = collectIssues();
        this.faults = collectFaults();
    }

    private Map<String, Commit> collectCommits() throws GitLabApiException {
        List<com.messners.gitlab.api.models.Commit> glCommits = this.gitlab.getCommitsAPI().getCommits(this.projectID);
        Map<String, Commit> commits = new HashMap<>();
        for (com.messners.gitlab.api.models.Commit glCommit : glCommits){
            commits.put(glCommit.getId(), new GitlabCommit(glCommit, getFiles(glCommit)));
        }
        return commits;
    }

    private Map<Integer, Issue> collectIssues() throws GitLabApiException {
        Map<Integer, Issue> issueMap = new HashMap<>();
        List<com.messners.gitlab.api.models.Issue> issueList = this.gitlab.getIssuesAPI().getIssues(this.projectID);
        for(com.messners.gitlab.api.models.Issue issue : issueList){
            issueMap.put(issue.getIid(), new Issue(issue));
        }
        return issueMap;
    }

    private List<Path> getFiles(com.messners.gitlab.api.models.Commit commit) throws GitLabApiException {
        List<Path> files = new ArrayList<>();
        List<Diff> commitDiffs = this.gitlab.getCommitsAPI().getDiffs(this.projectID, commit.getId());
        for(Diff diff : commitDiffs){
            files.add(Paths.get(diff.getNewPath()));
        }
        return files;
    }
}
