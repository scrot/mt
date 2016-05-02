package git.crawler;

import com.messners.gitlab.api.GitLabApiException;
import git.api.GitlabAPI;
import git.model.Issue;
import git.model.Project;
import git.repository.GLRepoBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/2/16.
 */
public class GitlabIssueCrawler implements IssueCrawler{
    private final GitlabAPI gitlab;
    private final Integer projectID;
    private final Map<Integer, Issue> issues;

    public GitlabIssueCrawler(Project project) throws GitLabApiException {
        GLRepoBuilder repoBuilder = new GLRepoBuilder(project);
        this.gitlab = repoBuilder.getGitlabApi();
        this.projectID = repoBuilder.getProjectID();
        this.issues = collectIssues();
    }

    @Override
    public Map<Integer, Issue> getIssues() {
        return this.issues;
    }

    private Map<Integer, Issue> collectIssues() throws GitLabApiException {
        Map<Integer, Issue> issueMap = new HashMap<>();
        List<com.messners.gitlab.api.models.Issue> issueList = this.gitlab.getIssuesAPI().getIssues(this.projectID);
        for(com.messners.gitlab.api.models.Issue issue : issueList){
            issueMap.put(issue.getIid(), new Issue(issue));
        }
        return issueMap;
    }
}
