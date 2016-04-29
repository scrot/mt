package git.project;

import com.messners.gitlab.api.GitLabApiException;
import git.crawler.GitlabCrawler;

import java.nio.file.Path;

public class GitlabProject implements Project {
    private final String gitHost;
    private final Path localPath;
    private final Integer id;
    private final String group;
    private final String project;
    private final String authToken;
    private final GitlabCrawler gitCrawler;

    public GitlabProject(Path localPath, String gitHost, String group, String project, String authToken) throws GitLabApiException {
        this.localPath = localPath;
        this.gitHost = gitHost;
        this.group = group;
        this.project = project;
        this.authToken = authToken;

        this.gitCrawler = new GitlabCrawler(this);
        this.id = this.gitCrawler.getProjectId();
    }

    public String getGitHost() {
        return gitHost;
    }

    @Override
    public Integer getId() {
        return id;
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
    public Path getLocalPath() {
        return this.localPath;
    }

    @Override
    public String getAuthToken() {
        return this.authToken;
    }

    @Override
    public GitlabCrawler getGitCrawler() {
        return this.gitCrawler;
    }
}
