package report;

import com.messners.gitlab.api.GitLabApiException;
import git.model.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigReader {
    private String name;
    private List<Project> projects;

    public ConfigReader(Path path) throws IOException, GitLabApiException {
        String filename = path.getFileName().toString();
        this.name = filename.substring(0, filename.indexOf('.'));
        BufferedReader reader = Files.newBufferedReader(path);
        this.projects = readGitlabProjects("", "", reader);
    }

    public String getName() {return name; }

    public List<Project> getProjects() {
        return projects;
    }

    private List<Project> readGitlabProjects(String hostUrl, String auth, BufferedReader reader) throws IOException, GitLabApiException {
        List<Project> projects = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] lineWords = line.split("\\s+");
            if(lineWords.length == 2) {
                Project project = new Project(Paths.get(lineWords[1]), hostUrl, "", lineWords[0], auth);
                projects.add(project);
            }
            else {
                throw new IOException();
            }
        }
        return projects;
    }
}