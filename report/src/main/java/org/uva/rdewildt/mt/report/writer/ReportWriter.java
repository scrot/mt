package org.uva.rdewildt.mt.report.writer;

import org.uva.rdewildt.mt.fpms.git.model.Project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReportWriter {
    public static void main(String[] args) throws Exception {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("/home/roy/Workspace/MT/mt/report/src/main/resources/example.conf");
        ConfigReader confReader = new ConfigReader(config);
        List<Project> projects = confReader.getProjects();
        OverviewReportBuilder obuilder = new OverviewReportBuilder(confReader.getName(), confReader.getProjects(), true, true);
        //FeatureReportsBuilder fbuilder = new FeatureReportsBuilder(projects, true, true);
        obuilder.writeReportsToFile();
        //fbuilder.writeReportsToFile();
    }
}
