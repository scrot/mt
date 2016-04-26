package git.repository;

import com.messners.gitlab.api.GitLabApiException;
import git.api.GitlabAPI;
import git.model.Project;


public class GLRepoBuilder implements RepoBuilder {
    private final GitlabAPI gitlab;
    private final Integer projectID;

    public GLRepoBuilder(Project project) throws GitLabApiException {
        this.gitlab = new GitlabAPI(project.getGitHost(), project.getAuthToken());
        this.projectID = this.gitlab.getProjectApi().getProject(project.getGroup(), project.getProject()).getId();
    }

    public GLRepoBuilder(String domainURL, String group, String project, String authToken) throws GitLabApiException {
        this.gitlab = new GitlabAPI(domainURL, authToken);
        this.projectID = this.gitlab.getProjectApi().getProject(group, project).getId();
    }

    public GitlabAPI getGitlabApi(){
        return this.gitlab;
    }

    public Integer getProjectID() {
        return this.projectID;
    }
}
