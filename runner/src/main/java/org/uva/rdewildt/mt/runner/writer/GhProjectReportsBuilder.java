package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.report.Report;
import org.uva.rdewildt.mt.gcrawler.github.GhProject;
import org.uva.rdewildt.mt.gcrawler.github.GhProjectCrawler;
import org.uva.rdewildt.mt.gcrawler.github.GhReport;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectReportsBuilder {
    private GhReport ghReport;

    public GhProjectReportsBuilder(String name, Integer numberRepos, Map<String,String> params, Path clonePath) {
        try {
            this.ghReport = new GhReport(name);
        } catch (NoSuchFieldException e) {e.printStackTrace();}
        Map<String, GhProject> ghProjects = new GhProjectCrawler(numberRepos, params, clonePath, false).getGhProjects();
        ghProjects.values().stream().forEach(project -> {
            try {
                this.ghReport.updateReport(project );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void writeReportsToFile(Path path) {
        try {
            this.ghReport.writeToFile(path, "", ',', true);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public Report getghProjectReport() {
        return ghReport;
    }
}
