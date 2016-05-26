package org.uva.rdewildt.mt.report.writer;

import org.uva.rdewildt.mt.fpms.Feature;
import org.uva.rdewildt.mt.fpms.FeatureCalculator;
import org.uva.rdewildt.mt.fpms.FeatureReport;
import org.uva.rdewildt.mt.fpms.git.model.Project;
import org.uva.rdewildt.mt.lims.Report;
import org.uva.rdewildt.mt.report.overview.Overview;
import org.uva.rdewildt.mt.report.overview.OverviewCalculator;
import org.uva.rdewildt.mt.report.overview.OverviewReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 5/26/16.
 */
public class OverviewReportBuilder {
    private final Report overviewReport;

    public OverviewReportBuilder(String name, List<Project> projects, Boolean ignoreGenerated, Boolean ignoreTests) {
        this.overviewReport = new OverviewReport(name);
        for(Project project : projects) {
            try {
                OverviewCalculator ocalc = new OverviewCalculator(project, ignoreGenerated, ignoreTests);
                this.overviewReport.updateReport(ocalc.getOverview().getValues());
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void writeReportsToFile() {
        try {
            this.overviewReport.writeToFile("_featureset", ',', true);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public Report getOverviewReport() {
        return overviewReport;
    }
}