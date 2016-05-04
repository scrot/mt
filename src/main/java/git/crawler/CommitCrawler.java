package git.crawler;

import git.model.Commit;

import java.util.Map;

/**
 * Created by roy on 4/30/16.
 */
public interface CommitCrawler {
    Map<Object, Commit> getCommits();
}
