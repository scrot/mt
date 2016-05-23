package org.uva.rdewildt.mt.featureset.git.crawler;

import org.uva.rdewildt.mt.featureset.git.model.Commit;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by roy on 4/30/16.
 */
public interface CommitCrawler {
    Map<String, Set<Commit>> getCommits();
}
