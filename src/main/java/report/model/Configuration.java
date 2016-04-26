package report.model;

import report.ReportBuilder;

import java.util.List;

public class Configuration {
    private final ReportBuilder reportBuilder;
    private final List<Project> projects;

    public Configuration(ReportBuilder reportBuilder, List<Project> projects) {
        this.reportBuilder = reportBuilder;
        this.projects = projects;
    }

    public ReportBuilder getGitHost() {
        return reportBuilder;
    }

    public List<Project> getProjects() {
        return projects;
    }
}
