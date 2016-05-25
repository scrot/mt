package org.uva.rdewildt.mt.fpms.git.crawler;

import org.uva.rdewildt.mt.fpms.git.model.Fault;

import java.util.Map;
import java.util.Set;

/**
 * Created by roy on 4/30/16.
 */
public interface FaultCrawler {
    Map<String, Set<Fault>> getFaults();
}
