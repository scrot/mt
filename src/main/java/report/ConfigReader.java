package report;

import com.messners.gitlab.api.GitLabApiException;
import git.project.GithubProject;
import git.project.GitlabProject;
import git.project.Project;

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

        String gitType = reader.readLine();
        if(gitType.equalsIgnoreCase("gitlab")){
            String hostUrl = reader.readLine();
            String auth = reader.readLine();
            this.projects = readGitlabProjects(hostUrl, auth, reader);
        }
        else if (gitType.equalsIgnoreCase("github")){
            String auth = reader.readLine();
            this.projects = readGithubProjects(auth, reader);
        }
        else {
            throw new IOException();
        }
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
            if(lineWords.length == 3) {
                Project project = new GitlabProject(Paths.get(lineWords[2]), hostUrl, lineWords[0], lineWords[1], auth);
                projects.add(project);
            }
            else {
                throw new IOException();
            }
        }
        return projects;
    }

    private List<Project> readGithubProjects(String auth, BufferedReader reader) throws IOException {
        List<Project> projects = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] lineWords = line.split("\\s+");
            if(lineWords.length == 3) {
                Project project = new GithubProject(Paths.get(lineWords[2]), lineWords[0], lineWords[1], auth);
                projects.add(project);
            }
            else {
                throw new IOException();
            }
        }
        return projects;
    }
}