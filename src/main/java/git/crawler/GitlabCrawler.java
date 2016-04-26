package git.crawler;

import com.messners.gitlab.api.GitLabApiException;
import com.messners.gitlab.api.models.Issue;
import git.model.SimpleCommit;
import git.model.SimpleIssue;
import git.repository.GLRepoBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitlabCrawler extends GitCrawler {
    public GitlabCrawler() {
        GLRepoBuilder glRepoBuilder = new GLRepoBuilder();
    }

    private List<SimpleCommit> getCommits(){
        return this.commits;
    }

    private List<SimpleIssue> getIssues(){
        return this.issues;
    }

    private Map<Integer, Issue> collectIssues() throws GitLabApiException {
        Map<Integer, Issue> issueMap = new HashMap<>();
        List<Issue> issueList = this.gitlab.getIssuesAPI().getIssues(this.projectID);
        for(Issue issue : issueList){
            issueMap.put(issue.getIid(), issue);
        }
        return issueMap;
    }
}
