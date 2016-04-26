package git.model;

public class Project {
    // Project identifiers
    private String gitHost;
    private String group;
    private String project;

    public Project(String gitHost, String group, String project) {
        this.gitHost = gitHost;
        this.group = group;
        this.project = project;
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
}
