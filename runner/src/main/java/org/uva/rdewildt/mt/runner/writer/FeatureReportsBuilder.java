package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.fpms.Feature;
import org.uva.rdewildt.mt.fpms.FeatureCalculator;
import org.uva.rdewildt.mt.fpms.FeatureCalculator2;
import org.uva.rdewildt.mt.fpms.FeatureReport;
import org.uva.rdewildt.mt.gcrawler.git.model.GReport;
import org.uva.rdewildt.mt.gcrawler.git.model.Project;
import org.uva.rdewildt.mt.report.Report;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FeatureReportsBuilder {
    private final List<Report> featureReports;

    public FeatureReportsBuilder(GReport greport, Boolean ignoreGenerated, Boolean ignoreTests) {
        this.featureReports = new ArrayList<>();
        List<String> header = greport.getHeader();
        greport.getBody().forEach(row -> {
            try {
                FeatureReport report = new FeatureReport(row.get(header.indexOf("Name")).toString());
                FeatureCalculator2 fcalc = new FeatureCalculator2(Paths.get(row.get(header.indexOf("BinaryPath")).toString()),
                        Paths.get(row.get(header.indexOf("GitPath")).toString()), ignoreGenerated, ignoreTests);

                for(Feature feature : fcalc.getFeatures().values()){
                    report.updateReport(feature);
                }

                featureReports.add(report);
            }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void writeReportsToFile(Path path) {
        for(Report featureReport : featureReports){
            try {
                featureReport.writeToFile(path, "_featureset", ',', true);
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    public List<Report> getFeatureReports() {
        return featureReports;
    }
}