package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.gcrawler.git.model.Project;
import org.uva.rdewildt.mt.ovms.OverviewCalculator;
import org.uva.rdewildt.mt.ovms.OverviewReport;
import org.uva.rdewildt.mt.report.Report;

import java.io.IOException;
import java.util.List;

/**
 * Created by roy on 5/26/16.
 */
public class OverviewReportBuilder {
    private final Report overviewReport;

    public OverviewReportBuilder(String name, List<Project> projects, Boolean ignoreGenerated, Boolean ignoreTests) {
        this.overviewReport = new OverviewReport(name);
        projects.stream().forEach(project -> {
            try {
                OverviewCalculator ocalc = new OverviewCalculator(project, ignoreGenerated, ignoreTests);
                this.overviewReport.updateReport(ocalc.getOverview().getValues());
            }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void writeReportsToFile() {
        try {
            this.overviewReport.writeToFile("", ',', true);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public Report getOverviewReport() {
        return overviewReport;
    }
}