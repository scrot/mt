package org.uva.rdewildt.mt.fpms.git.model;

import java.nio.file.Path;

public class Project {
    private final String gitHost;
    private final Path localPath;
    private final Path jarPath;
    private final String group;
    private final String project;
    private final String authToken;

    public Project(Path localPath, Path jarPath, String gitHost, String group, String project, String authToken) {
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

    public Path getGitRoot() {
        return this.localPath;
    }

    public Path getBinaryRoot() {
        return jarPath;
    }

    public String getAuthToken() {
        return this.authToken;
    }
}
