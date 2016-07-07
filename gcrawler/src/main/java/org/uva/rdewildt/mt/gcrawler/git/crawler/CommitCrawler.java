package org.uva.rdewildt.mt.gcrawler.git.crawler;

import org.uva.rdewildt.mt.gcrawler.git.model.Commit;

import java.util.*;
/**
 * Created by roy on 5/26/16.
 */
public interface CommitCrawler {
    Map<String, Set<Commit>> getChanges();
}
