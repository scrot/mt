package org.uva.rdewildt.mt.gcrawler.git;

import org.uva.rdewildt.mt.utils.model.git.Commit;

import java.util.Map;
import java.util.Set;

/**
 * Created by roy on 5/26/16.
 */
public interface CommitCrawler {
    Map<String, Set<Commit>> getChanges();
}
