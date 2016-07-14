package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.utils.model.git.GReport;
import org.uva.rdewildt.mt.utils.model.git.Project;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ReportWriter {
    private static Path config;
    private static Path output;

    public static void main(String[] args) throws IOException, NoSuchFieldException {
        loadRinseConfig();

        assert config != null && output != null;
        ovBuilder(config,output);
        fBuilder(config, output);
    }

    public static void fBuilder(Path config, Path output) throws IOException, NoSuchFieldException {
        GReport testReport = new GReport(config);
        System.out.println("Building feature reports");
        FeatureReportsBuilder fbuilder = new FeatureReportsBuilder(testReport, true, true, true);
        fbuilder.writeReportsToFile(output);
    }

    private static void ghBuilder(Path config, Path output) {
        Map<String, String> params = new HashMap<String, String>() {{
            put("language", "Java");
        }};
        GhProjectReportsBuilder ghbuilder = new GhProjectReportsBuilder("top1000", 1000, params, config);
        ghbuilder.writeReportsToFile(config);
    }

    private static void ovBuilder(Path config, Path output) throws IOException, NoSuchFieldException {
        GReport testReport = new GReport(config);
        Map<String, Path> ovinput = new HashMap<>();
        testReport.getReport().forEach(reportable -> {
            Project project = (Project) reportable;
            ovinput.put(project.getId(), project.getGitRoot());
        });

        System.out.println("Building overview report");
        OverviewReportBuilder obuilder = new OverviewReportBuilder("systems", ovinput, true, true, true);
        obuilder.writeReportsToFile(output);
    }

    private static void loadLinuxHomeConfig(){
        config = Paths.get("/home/roy/Workspace/MT/mt/runner/src/main/resources/linux_home.csv");
        output = Paths.get("/home/roy/Workspace/MT");
    }

    private static void loadWindowsIsConfig(){
        config = Paths.get("C:\\Users\\royw\\Workspace\\mt\\runner\\src\\main\\resources\\windows_is.csv");
        output = Paths.get("C:\\Users\\royw\\Workspace");
    }

    private static void loadRinseConfig(){
        config = Paths.get("/home/mp44cc/ROY/rinse.csv");
        output = Paths.get("/home/mp44cc/ROY/");
    }
}
