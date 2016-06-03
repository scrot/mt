package org.uva.rdewildt.mt.gcrawler.github;

import org.uva.rdewildt.mt.report.Report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by roy on 5/27/16.
 */
public class GhReport extends Report {
    public GhReport(Path filePath) throws IOException {
        super(filePath, new GhProject());
    }

    public GhReport(String name) {
        super(name, new GhProject());
    }
}
