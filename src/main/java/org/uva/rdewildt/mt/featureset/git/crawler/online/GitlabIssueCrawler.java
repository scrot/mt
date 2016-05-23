package org.uva.rdewildt.mt.featureset.git.crawler.online;

import com.messners.gitlab.api.GitLabApiException;
import org.uva.rdewildt.mt.featureset.git.api.GitlabAPI;
import org.uva.rdewildt.mt.featureset.git.crawler.IssueCrawler;
import org.uva.rdewildt.mt.featureset.git.model.Issue;
import org.uva.rdewildt.mt.featureset.git.model.Project;
import org.uva.rdewildt.mt.featureset.git.repository.GLRepoBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/2/16.
 */
public class GitlabIssueCrawler implements IssueCrawler {
    private final GitlabAPI gitlab;
    private final Integer projectID;
    private Map<Integer, Issue> issues;

    public GitlabIssueCrawler(Project project) throws GitLabApiException {
        GLRepoBuilder repoBuilder = new GLRepoBuilder(project);
        this.gitlab = repoBuilder.getGitlabApi();
        this.projectID = repoBuilder.getProjectID();
    }

    @Override
    public Map<Integer, Issue> getIssues() {
        if(this.issues == null){
            this.issues = collectIssues(gitlab, projectID);
        }
        return this.issues;
    }

    private Map<Integer, Issue> collectIssues(GitlabAPI gitlab, Integer projectID) {
        Map<Integer, Issue> issueMap = new HashMap<>();
        List<com.messners.gitlab.api.models.Issue> issueList = null;
        try {
            issueList = gitlab.getIssuesAPI().getIssues(projectID);
            for(com.messners.gitlab.api.models.Issue issue : issueList){
                issueMap.put(issue.getIid(), new Issue(issue));
            }
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
        return issueMap;
    }
}
