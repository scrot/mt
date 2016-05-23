package org.uva.rdewildt.mt.report;

import org.uva.rdewildt.mt.featureset.git.model.Project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReportWriter {
    public static void main(String[] args) throws Exception {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("/home/roy/Workspace/MT/mt/src/main/resources/example.conf");
        ConfigReader confReader = new ConfigReader(config);
        List<Project> projects = confReader.getProjects();
        //OverviewReports builder = new OverviewReports(confReader.getName(), projects);
        //builder.writeOverviewReportToFile(", ");
        FeatureReports fbuilder = new FeatureReports(projects);
        fbuilder.writeOverviewReportToFile();
    }
}
