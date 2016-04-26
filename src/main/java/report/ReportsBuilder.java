package report;

import report.model.Configuration;
import report.model.Report;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReportsBuilder {
    private final List<Report> reports;

    public ReportsBuilder(Path configFile) throws IOException {
        Configuration config = ConfigReader.buildConfigFromPath(configFile);
        this.reports = buildReports(config);
    }

    private List<Report> buildReports(Configuration config) {
        List<Report> reports = new ArrayList<>();
        for(Project project : config.getProjects()){
            reports.add((config.getGitHost().buildReport(project)));
        }

        return reports;
    }

    public List<Report> getReports() {
        return reports;
    }
}
