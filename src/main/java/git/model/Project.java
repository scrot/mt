package git.model;

import git.crawler.GitCrawler;

public interface Project {
    String getGroup();
    String getProject();
    String getAuthToken();
    GitCrawler getGitCrawler();
}
