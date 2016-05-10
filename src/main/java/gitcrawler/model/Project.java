package gitcrawler.model;

import com.messners.gitlab.api.GitLabApiException;

import java.nio.file.Path;

public class Project {
    private final String gitHost;
    private final Path localPath;
    private final Path jarPath;
    private final String group;
    private final String project;
    private final String authToken;

    public Project(Path localPath, Path jarPath, String gitHost, String group, String project, String authToken) throws GitLabApiException {
        this.localPath = localPath;
        this.jarPath = jarPath;
        this.gitHost = gitHost;
        this.group = group;
        this.project = project;
        this.authToken = authToken;
    }

    public String getGitHost() {
        return gitHost;
    }

    public String getGroup() {
        return this.group;
    }

    public String getProject() {
        return this.project;
    }

    public Path getLocalPath() {
        return this.localPath;
    }

    public Path getJarPath() {
        return jarPath;
    }

    public String getAuthToken() {
        return this.authToken;
    }
}
