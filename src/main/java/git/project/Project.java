package git.project;

import git.crawler.GitCrawler;

import java.nio.file.Path;

public interface Project {
    Integer getId();
    String getGroup();
    String getProject();
    String getAuthToken();
    Path getLocalPath();
    GitCrawler getGitCrawler();
}
