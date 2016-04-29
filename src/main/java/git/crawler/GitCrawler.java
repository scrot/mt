package git.crawler;

import git.model.*;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GitCrawler {
    Integer getProjectId();
    Date createdAt();
    Date lastModified();
    Map<String, Commit> getCommits();
    Map<Integer, Issue> getIssues();
    Map<Path, List<Fault>> getFaults();
    Map<Path, List<Commit>> getChanges();
    Map<Path, Set<Author>> getAuthors();
}
