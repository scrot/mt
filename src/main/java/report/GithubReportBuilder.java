package report;

import report.model.Report;

public class GithubReportBuilder extends ReportBuilder {
    private final String oAuthToken;

    public GithubReportBuilder(String oAuthToken) {
        this.oAuthToken = oAuthToken;
    }

    @Override
    public Report buildReport(Project project) {
        return null;
    }
}
