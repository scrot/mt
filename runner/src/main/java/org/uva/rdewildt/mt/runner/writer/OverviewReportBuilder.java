package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.gcrawler.git.model.Project;
import org.uva.rdewildt.mt.ovms.OverviewCalculator;
import org.uva.rdewildt.mt.ovms.OverviewReport;
import org.uva.rdewildt.mt.report.Report;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/26/16.
 */
public class OverviewReportBuilder {
    private final Report overviewReport;

    public OverviewReportBuilder(String name, Map<String, Path> projects, Boolean ignoreGenerated, Boolean ignoreTests) {
        this.overviewReport = new OverviewReport(name);
        projects.forEach((k,v) -> {
            try {
                OverviewCalculator ocalc = new OverviewCalculator(k, v, ignoreGenerated, ignoreTests);
                this.overviewReport.updateReport(ocalc.getOverview().getValues());
            }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void writeReportsToFile(Path path) {
        try {
            this.overviewReport.writeToFile(path, "_overview", ',', true);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public Report getOverviewReport() {
        return overviewReport;
    }
}