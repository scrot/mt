package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.gcrawler.git.GitUtils;
import org.uva.rdewildt.mt.gcrawler.git.model.GReport;
import org.uva.rdewildt.mt.gcrawler.git.model.Project;
import org.uva.rdewildt.mt.gcrawler.github.GhReport;
import org.uva.rdewildt.mt.report.Report;
import org.uva.rdewildt.mt.utils.MapUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportWriter {
    public static void main(String[] args) throws Exception {
        //Path top1000 = Paths.get("/home/roy/Workspace/MT/mt/dataset/original/top1000.csv");
        //Report top1000Report = new GhReport(top1000);
        Path config = Paths.get("C:\\Users\\royw\\Workspace\\mt\\runner\\src\\main\\resources\\opensource.conf");
        Path output = Paths.get("C:\\Users\\royw\\Workspace\\");

        GReport testReport = new GReport(config);

        Map<String, Path> ovinput = new HashMap<>();
        testReport.getReport().forEach(reportable -> {
            Project project = (Project) reportable;
            ovinput.put(project.getId(), project.getGitRoot());
        });

        OverviewReportBuilder obuilder = new OverviewReportBuilder("systems",ovinput, true, true);
        obuilder.writeReportsToFile(output);

        FeatureReportsBuilder fbuilder = new FeatureReportsBuilder(testReport, true, true, true);
        fbuilder.writeReportsToFile(output);
    }

    private static void GhBuilder(Path top1000) {
        //GhBuilder
        Map<String, String> params = new HashMap<String, String>(){{put("language", "Java");}};
        GhProjectReportsBuilder ghbuilder = new GhProjectReportsBuilder("top1000", 1000, params, top1000);
        ghbuilder.writeReportsToFile(top1000);
    }
}
