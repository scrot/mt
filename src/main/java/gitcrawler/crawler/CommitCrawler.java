package gitcrawler.crawler;

import gitcrawler.model.Commit;

import java.util.Map;

/**
 * Created by roy on 4/30/16.
 */
public interface CommitCrawler {
    Map<Object, Commit> getCommits();
}
