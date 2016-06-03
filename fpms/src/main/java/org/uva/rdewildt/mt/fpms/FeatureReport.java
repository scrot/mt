package org.uva.rdewildt.mt.fpms;

import org.uva.rdewildt.mt.report.Report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by roy on 5/23/16.
 */
public class FeatureReport extends Report {
    public FeatureReport(Path filePath) throws IOException {
        super(filePath, new Feature());
    }

    public FeatureReport(String name) {
        super(name, new Feature());
    }
}
