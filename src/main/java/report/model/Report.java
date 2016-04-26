package report.model;

import distr.CodeDistribution;
import distr.FaultDistribution;
import git.model.Project;

public class Report {
    private final Project projectData;
    private final GitData gitdata;
    private final SourceData sourceData;

    // Distributions
    private CodeDistribution codeDistribution;
    private FaultDistribution faultDistribution;

    public Report(Project projectData, GitData gitdata, SourceData sourceData, CodeDistribution codeDistribution, FaultDistribution faultDistribution) {
        this.projectData = projectData;
        this.gitdata = gitdata;
        this.sourceData = sourceData;
        this.codeDistribution = codeDistribution;
        this.faultDistribution = faultDistribution;
    }

    public Project getProjectData() {
        return projectData;
    }

    public GitData getGitdata() {
        return gitdata;
    }

    public SourceData getSourceData() {
        return sourceData;
    }

    public CodeDistribution getCodeDistribution() {
        return codeDistribution;
    }

    public FaultDistribution getFaultDistribution() {
        return faultDistribution;
    }
}
