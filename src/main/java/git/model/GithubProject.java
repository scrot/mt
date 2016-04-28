package git.model;

import git.crawler.GitlabCrawler;

import java.util.regex.Pattern;

public class GithubProject implements Project {

    public GithubProject(String group, String project, String authToken) {
        //Pattern.compile( "(?i)((fix(es|ed)?|resolve(s|d)?|close(s|d)?)(.*/.*|\\\\s*)#\\\\d+)+");
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
    public GitlabCrawler getGitCrawler() {
        return null;
    }
}
