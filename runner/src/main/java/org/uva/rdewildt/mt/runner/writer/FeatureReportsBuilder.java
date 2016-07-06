package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.fpms.Feature;
import org.uva.rdewildt.mt.fpms.FeatureCalculator;
import org.uva.rdewildt.mt.fpms.FeatureReport;
import org.uva.rdewildt.mt.gcrawler.git.model.GReport;
import org.uva.rdewildt.mt.report.Report;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FeatureReportsBuilder {
    private final List<Report> featureReports;

    public FeatureReportsBuilder(GReport greport, Boolean ignoreGenerated, Boolean ignoreTests, Boolean onlyOuterClasses, Boolean stateAware) {
        this.featureReports = new ArrayList<>();
        List<String> header = greport.getHeader();
        greport.getBody().forEach(row -> {
            try {
                FeatureReport report = new FeatureReport(row.get(header.indexOf("Name")).toString());
                System.out.println("\tBuilding report " + report.getName());
                FeatureCalculator fcalc = new FeatureCalculator(Paths.get(row.get(header.indexOf("BinaryPath")).toString()),
                        Paths.get(row.get(header.indexOf("GitPath")).toString()), ignoreGenerated, ignoreTests, onlyOuterClasses, stateAware);

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
            featureReport.writeToFile(path, "_featureset", ',', true);
        }
    }

    public List<Report> getFeatureReports() {
        return featureReports;
    }
}