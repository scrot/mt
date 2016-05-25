package org.uva.rdewildt.mt.fpms.git.crawler;

import org.uva.rdewildt.mt.fpms.git.model.Commit;

import java.util.Map;
import java.util.Set;

/**
 * Created by roy on 4/30/16.
 */
public interface ClassCommitCrawler {
    Map<String, Set<Commit>> getCommits();
}
