package faults.repository;

import com.messners.gitlab.api.GitLabApiException;
import faults.crawler.GitlabAPI;


public class GLRepoBuilder implements RepoBuilder {
    private final GitlabAPI gitlab;
    private final Integer projectID;

    public GLRepoBuilder(String domainURL, String group, String project, String oAuthToken) throws GitLabApiException {
        this.gitlab = new GitlabAPI(domainURL, oAuthToken);
        this.projectID = this.gitlab.getProjectApi().getProject(group, project).getId();
    }

    public GitlabAPI getGitlabApi(){
        return this.gitlab;
    }

    public Integer getProjectID() {
        return this.projectID;
    }
}
