package git.model;

import com.messners.gitlab.api.GitLabApiException;
import git.crawler.GitCrawler;
import git.crawler.GitlabCrawler;

public class GitlabProject implements Project {
    private final String group;
    private final String project;
    private final String authToken;
    private final String gitHost;
    private final GitCrawler gitCrawler;

    public GitlabProject(String gitHost, String group, String project, String authToken) throws GitLabApiException {
        this.gitHost = gitHost;
        this.group = group;
        this.project = project;
        this.authToken = authToken;

        this.gitCrawler = new GitlabCrawler(this);
    }

    public String getGitHost() {
        return gitHost;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public String getProject() {
        return this.project;
    }

    @Override
    public String getAuthToken() {
        return this.authToken;
    }

    @Override
    public GitCrawler getGitCrawler() {
        return this.gitCrawler;
    }
}
