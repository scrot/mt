package report;

import report.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigReader {
    public static Configuration buildConfigFromPath(Path path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path);

        // setup GitHost
        String line = reader.readLine();
        ReportBuilder githost = null;
        if(line.equalsIgnoreCase("gitlab")){
            String hostUrl = reader.readLine();
            String privateToken = reader.readLine();
            githost = new GitlabReportBuilder(privateToken, hostUrl);
        }
        else if (line.equalsIgnoreCase("github")){
            String oAuthToken = reader.readLine();
            githost = new GithubReportBuilder(oAuthToken);
        }
        else {
            throw new IOException();
        }

        // Get Projects
        List<Project> projects = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] lineWords = line.split("\\s+");
            if(lineWords.length == 2) {
                Project project = new Project(lineWords[0], lineWords[1]);
                projects.add(project);
            }
            else {
                throw new IOException();
            }
        }

        return new Configuration(githost, projects);
    }
}