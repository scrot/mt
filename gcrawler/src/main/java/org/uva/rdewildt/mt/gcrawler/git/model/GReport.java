package org.uva.rdewildt.mt.gcrawler.git.model;

import org.uva.rdewildt.mt.report.Report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by roy on 6/7/16.
 */
public class GReport extends Report {
    public GReport(Path filePath) throws IOException, NoSuchFieldException {
        super(filePath, new Project());
    }

    public GReport(String name) {
        super(name, new Project());
    }
}
