package faults.repository;

import org.gitlab.api.GitlabAPI;

import java.io.IOException;

public class GLRepoBuilder implements RepoBuilder {
    private final GitlabAPI gitLab;

    public GLRepoBuilder(String domainURL, String oAuthToken) throws IOException {
        this.gitLab = GitlabAPI.connect(domainURL, oAuthToken);
    }

    public GitlabAPI getRepository(Integer projectID){
        return this.gitLab;
    }
}
