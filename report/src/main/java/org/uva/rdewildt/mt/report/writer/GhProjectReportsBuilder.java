package org.uva.rdewildt.mt.report.writer;

import org.uva.rdewildt.mt.lims.Report;
import org.uva.rdewildt.mt.gcrawler.github.GhProject;
import org.uva.rdewildt.mt.gcrawler.github.GhProjectCrawler;
import org.uva.rdewildt.mt.gcrawler.github.GhProjectReport;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectReportsBuilder {
    private final GhProjectReport ghProjectReport;

    public GhProjectReportsBuilder(String name, Integer numberRepos, Map<String,String> params) {
        this.ghProjectReport = new GhProjectReport(name);
        List<GhProject> ghProjects = new GhProjectCrawler(numberRepos, params).getGhProjects();
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
