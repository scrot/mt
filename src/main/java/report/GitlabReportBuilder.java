package report;

import report.model.GitData;
import git.model.Project;
import report.model.Report;

public class GitlabReportBuilder extends ReportBuilder {
    private final String privateToken;
    private final String hostUrl;

    public GitlabReportBuilder(String privateToken, String hostUrl) {
        this.privateToken = privateToken;
        this.hostUrl = hostUrl;
    }

    @Override
    public Report buildReport(Project project) {
        Project projectData = new Project(
                this.hostUrl,
                project.getGroup(),
                project.getProject());

        GitData gitData = new GitData(

        );

        return null;
    }
}
