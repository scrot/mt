package report;

import git.model.Project;
import report.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigReader {
    public static List<Project> getProjectsFromPath(Path path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path);

        String hostUrl;
        String line = reader.readLine();
        if(line.equalsIgnoreCase("gitlab")){
            hostUrl = reader.readLine();
        }
        else if (line.equalsIgnoreCase("github")){
            hostUrl = "https://github.com";
        }
        else {
            throw new IOException();
        }
        String auth = reader.readLine();

        return readProjects(hostUrl,auth,reader);
    }

    private static List<Project> readProjects(String hostUrl, String auth, BufferedReader reader) throws IOException {
        List<Project> projects = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] lineWords = line.split("\\s+");
            if(lineWords.length == 2) {
                Project project = new Project(hostUrl, lineWords[0], lineWords[1], auth);
                projects.add(project);
            }
            else {
                throw new IOException();
            }
        }
        return projects;
    }
}