package report;

import distr.CodeDistribution;
import distr.FaultDistribution;
import git.crawler.GitCrawler;
import git.model.Project;

public interface ReportBuilder {
    Project getProject();

}
