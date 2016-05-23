package org.uva.rdewildt.mt.featureset.git.crawler;

import org.uva.rdewildt.mt.featureset.git.model.Fault;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 4/30/16.
 */
public interface FaultCrawler {
    Map<Path, List<Fault>> getFaults();
}
