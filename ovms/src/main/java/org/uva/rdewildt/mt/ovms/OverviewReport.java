package org.uva.rdewildt.mt.ovms;

import org.uva.rdewildt.mt.report.Report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by roy on 5/26/16.
 */
public class OverviewReport extends Report {
    public OverviewReport(Path filePath) throws IOException, NoSuchFieldException {
        super(filePath, new Overview());
    }

    public OverviewReport(String name) {
        super(name, new Overview());
    }
}
