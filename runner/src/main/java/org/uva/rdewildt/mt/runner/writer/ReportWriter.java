package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.gcrawler.git.model.GReport;
import org.uva.rdewildt.mt.gcrawler.git.model.Project;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ReportWriter {
    public static void main(String[] args) throws IOException, NoSuchFieldException {
        Path config = Paths.get("/home/roy/Workspace/MT/mt/runner/src/main/resources/linux_home.csv");
        Path output = Paths.get("/home/roy/Workspace/MT");
        fBuilder(config,output);
    }

    private void ghBuilder(Path config, Path output) {
        Map<String, String> params = new HashMap<String, String>(){{put("language", "Java");}};
        GhProjectReportsBuilder ghbuilder = new GhProjectReportsBuilder("top1000", 1000, params, config);
        ghbuilder.writeReportsToFile(config);
    }

    private void ovBuilder(Path config, Path output) throws IOException, NoSuchFieldException {
        GReport testReport = new GReport(config);
        Map<String, Path> ovinput = new HashMap<>();
        testReport.getReport().forEach(reportable -> {
            Project project = (Project) reportable;
            ovinput.put(project.getId(), project.getGitRoot());
        });

        System.out.println("Building overview report");
        OverviewReportBuilder obuilder = new OverviewReportBuilder("systems",ovinput, true, true);
        obuilder.writeReportsToFile(output);
    }

    private static void fBuilder(Path config, Path output) throws IOException, NoSuchFieldException {
        GReport testReport = new GReport(config);
        System.out.println("Building feature reports");
        FeatureReportsBuilder fbuilder = new FeatureReportsBuilder(testReport, true, true, true, true);
        fbuilder.writeReportsToFile(output);
    }
}
