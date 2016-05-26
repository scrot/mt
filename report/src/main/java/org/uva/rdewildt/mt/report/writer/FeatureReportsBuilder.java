package org.uva.rdewildt.mt.report.writer;

import org.uva.rdewildt.mt.fpms.Feature;
import org.uva.rdewildt.mt.fpms.FeatureCalculator;
import org.uva.rdewildt.mt.fpms.FeatureReport;
import org.uva.rdewildt.mt.fpms.git.model.Project;
import org.uva.rdewildt.mt.lims.Report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeatureReportsBuilder {
    private final List<Report> featureReports;

    public FeatureReportsBuilder(List<Project> projects, Boolean ignoreGenerated, Boolean ignoreTests) {
        this.featureReports = new ArrayList<>();
        for(Project project : projects) {
            try {
                FeatureReport report = new FeatureReport(project.getProject());
                FeatureCalculator fcalc = new FeatureCalculator(project.getBinaryRoot(), project.getGitRoot(), ignoreGenerated, ignoreTests);

                for(Feature feature : fcalc.getFeatures().values()){
                    report.updateReport(feature.getValues());
                }

                featureReports.add(report);
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void writeReportsToFile() {
        for(Report featureReport : featureReports){
            try {
                featureReport.writeToFile("_featureset", ',', true);
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    public List<Report> getFeatureReports() {
        return featureReports;
    }
}