package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.report.Report;
import org.uva.rdewildt.mt.gcrawler.github.GhProject;
import org.uva.rdewildt.mt.gcrawler.github.GhProjectCrawler;
import org.uva.rdewildt.mt.gcrawler.github.GhProjectReport;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectReportsBuilder {
    private final GhProjectReport ghProjectReport;

    public GhProjectReportsBuilder(String name, Integer numberRepos, Map<String,String> params, Path clonePath) {
        this.ghProjectReport = new GhProjectReport(name);
        Set<GhProject> ghProjects = new GhProjectCrawler(numberRepos, params,clonePath).getGhProjects();
        ghProjects.stream().forEach(project -> {
            try {
                this.ghProjectReport.updateReport(project.getValues());
            } catch (NoSuchFieldException e) { e.printStackTrace(); }
        });
    }

    public void writeReportsToFile(Path path) {
        try {
            this.ghProjectReport.writeToFile(path, "", ',', true);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public Report getghProjectReport() {
        return ghProjectReport;
    }
}
