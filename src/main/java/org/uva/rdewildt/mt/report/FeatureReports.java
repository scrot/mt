package org.uva.rdewildt.mt.report;

import org.uva.rdewildt.lims.Report;
import org.uva.rdewildt.mt.featureset.Feature;
import org.uva.rdewildt.mt.featureset.FeatureCalculator;
import org.uva.rdewildt.mt.featureset.FeatureReport;
import org.uva.rdewildt.mt.featureset.git.model.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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