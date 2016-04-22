package faults.api;

import com.messners.gitlab.api.GitLabApi;

public class GitlabAPI extends GitLabApi {
    private IssuesAPI issuesAPI;
    private CommitsAPI commitsAPI;

    public GitlabAPI(String hostUrl, String privateToken) {
        super(hostUrl, privateToken);
        this.issuesAPI = new IssuesAPI(this);
        this.commitsAPI = new CommitsAPI(this);
    }

    public IssuesAPI getIssuesAPI() {
        return (issuesAPI);
    }

    public CommitsAPI getCommitsAPI() {
        return (commitsAPI);
    }
}
