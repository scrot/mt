package org.uva.rdewildt.mt.report.writer;

import org.kohsuke.github.GHRepositorySearchBuilder;
import org.uva.rdewildt.mt.lims.Report;
import org.uva.rdewildt.mt.report.ghcrawler.GhProject;
import org.uva.rdewildt.mt.report.ghcrawler.GhProjectCalculator;
import org.uva.rdewildt.mt.report.ghcrawler.GhProjectReport;

import java.io.IOException;
import java.util.List;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectReportsBuilder {
    private final GhProjectReport ghProjectReport;

    public GhProjectReportsBuilder(String name, Integer numberRepos, String ofLanguage, GHRepositorySearchBuilder.Sort sortBy) {
        this.ghProjectReport = new GhProjectReport(name);
        List<GhProject> ghProjects = new GhProjectCalculator(numberRepos, ofLanguage, sortBy).getGhProjects();
        ghProjects.stream().forEach(project -> {
            try {
                this.ghProjectReport.updateReport(project.getValues());
            } catch (NoSuchFieldException e) { e.printStackTrace(); }
        });
    }

    public void writeReportsToFile() {
        try {
            this.ghProjectReport.writeToFile("", ',', true);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public Report getghProjectReport() {
        return ghProjectReport;
    }
}
