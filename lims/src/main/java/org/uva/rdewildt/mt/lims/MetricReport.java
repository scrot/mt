package org.uva.rdewildt.mt.lims;

import org.uva.rdewildt.mt.report.Report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by roy on 5/23/16.
 */
public class MetricReport extends Report {
    public MetricReport(Path filePath) throws IOException {
        super(filePath, new Metric());
    }

    public MetricReport(String name) {
        super(name, new Metric());
    }
}
