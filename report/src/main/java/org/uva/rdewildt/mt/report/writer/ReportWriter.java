package org.uva.rdewildt.mt.report.writer;

import org.kohsuke.github.GHRepositorySearchBuilder;
import org.uva.rdewildt.mt.fpms.git.model.Project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReportWriter {
    public static void main(String[] args) throws Exception {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("/home/roy/Workspace/MT/mt/report/src/main/resources/opensource.conf");
        ConfigReader confReader = new ConfigReader(config);
        List<Project> projects = confReader.getProjects();
        GhProjectReportsBuilder ghbuilder = new GhProjectReportsBuilder("top100starred", 100, "java", GHRepositorySearchBuilder.Sort.STARS);
        ghbuilder.writeReportsToFile();
        //OverviewReportBuilder obuilder = new OverviewReportBuilder(confReader.getName(), confReader.getProjects(), true, true);
        //obuilder.writeReportsToFile();
        //FeatureReportsBuilder fbuilder = new FeatureReportsBuilder(projects, true, true);
        //fbuilder.writeReportsToFile();
    }
}
