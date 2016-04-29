package git.project;

import git.crawler.GitlabCrawler;

import java.nio.file.Path;

public class GithubProject implements Project {

    public GithubProject(Path localPath, String group, String project, String authToken) {
        //Pattern.compile( "(?i)((fix(es|ed)?|resolve(s|d)?|close(s|d)?)(.*/.*|\\\\s*)#\\\\d+)+");
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public String getProject() {
        return null;
    }

    @Override
    public String getAuthToken() {
        return null;
    }

    @Override
    public Path getLocalPath() {
        return null;
    }

    @Override
    public GitlabCrawler getGitCrawler() {
        return null;
    }
}
