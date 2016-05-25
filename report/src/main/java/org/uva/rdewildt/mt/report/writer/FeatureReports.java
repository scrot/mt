package org.uva.rdewildt.mt.report.writer;

import org.uva.rdewildt.mt.fpms.Feature;
import org.uva.rdewildt.mt.fpms.FeatureCalculator;
import org.uva.rdewildt.mt.fpms.FeatureReport;
import org.uva.rdewildt.mt.fpms.git.model.Project;
import org.uva.rdewildt.mt.lims.Report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeatureReports {
    private final List<Report> featureReports;

    public FeatureReports(List<Project> projects) {
        this.featureReports = new ArrayList<>();
        for(Project project : projects) {
            try {
                FeatureReport report = new FeatureReport(project.getProject());
                FeatureCalculator fcalc = new FeatureCalculator(project.getBinaryRoot(), project.getGitRoot());

                for(Feature feature : fcalc.getFeatures().values()){
                    report.updateReport(feature.getFeatures());
                }

                featureReports.add(report);
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void writeOverviewReportToFile() {
        for(Report featureReport : featureReports){
            try {
                featureReport.toCSV("_featureset");
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    public List<Report> getFeatureReports() {
        return featureReports;
    }
}