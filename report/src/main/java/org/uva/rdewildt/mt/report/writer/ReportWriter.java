package org.uva.rdewildt.mt.report.writer;

import org.uva.rdewildt.mt.gcrawler.git.model.Project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportWriter {
    public static void main(String[] args) throws Exception {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("/home/roy/Workspace/MT/mt/report/src/main/resources/opensource.conf");
        ConfigReader confReader = new ConfigReader(config);
        List<Project> projects = confReader.getProjects();

        Map<String, String> params = new HashMap<String, String>(){{
            put("language", "Java");
        }};
        GhProjectReportsBuilder ghbuilder = new GhProjectReportsBuilder("alljava", 1000, params);
        ghbuilder.writeReportsToFile();
        //OverviewReportBuilder obuilder = new OverviewReportBuilder(confReader.getName(), confReader.getProjects(), true, true);
        //obuilder.writeReportsToFile();
        //FeatureReportsBuilder fbuilder = new FeatureReportsBuilder(projects, true, true);
        //fbuilder.writeReportsToFile();
    }
}
