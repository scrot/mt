package git.repository;

import com.messners.gitlab.api.GitLabApiException;
import git.api.GitlabAPI;


public class GLRepoBuilder implements RepoBuilder {
    private final GitlabAPI gitlab;
    private final Integer projectID;

    public GLRepoBuilder(String domainURL, String group, String project, String repoBuilder) throws GitLabApiException {
        this.gitlab = new GitlabAPI(domainURL, repoBuilder);
        this.projectID = this.gitlab.getProjectApi().getProject(group, project).getId();
    }

    public GitlabAPI getGitlabApi(){
        return this.gitlab;
    }

    public Integer getProjectID() {
        return this.projectID;
    }
}
