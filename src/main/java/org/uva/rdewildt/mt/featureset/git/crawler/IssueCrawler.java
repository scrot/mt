package org.uva.rdewildt.mt.featureset.git.crawler;

import org.uva.rdewildt.mt.featureset.git.model.Issue;

import java.util.Map;

/**
 * Created by roy on 4/30/16.
 */
public interface IssueCrawler {
    Map<Integer, Issue> getIssues();
}
