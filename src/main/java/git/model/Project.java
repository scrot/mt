package git.model;

public class Project {
    // Project identifiers
    private String gitHost;
    private String group;
    private String project;
    private String authToken;

    public Project(String gitHost, String group, String project, String authToken) {
        this.gitHost = gitHost;
        this.group = group;
        this.project = project;
        this.authToken = authToken;
    }

    public String getGitHost() {
        return gitHost;
    }

    public String getGroup() {
        return group;
    }

    public String getProject() {
        return project;
    }

    public String getAuthToken() {
        return authToken;
    }
}
