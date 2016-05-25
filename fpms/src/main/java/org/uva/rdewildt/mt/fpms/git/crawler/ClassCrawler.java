package org.uva.rdewildt.mt.fpms.git.crawler;

import org.uva.rdewildt.mt.fpms.git.model.Author;
import org.uva.rdewildt.mt.fpms.git.model.Commit;
import org.uva.rdewildt.mt.fpms.git.model.Fault;
import org.uva.rdewildt.mt.fpms.git.model.Issue;

import java.util.*;

/**
 * Created by roy on 5/5/16.
 */
public interface ClassCrawler {
    Map<String, Set<Commit>> getCommits();
    Map<String, Set<Fault>> getFaults();
    Map<String, Set<Author>> getAuthors();

    Map<Integer, Issue> getIssues();


}
