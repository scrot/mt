package report;

import com.messners.gitlab.api.GitLabApiException;
import git.model.GithubProject;
import git.model.GitlabProject;
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
    private List<Project> projects;

    public ConfigReader(Path path) throws IOException, GitLabApiException {
        BufferedReader reader = Files.newBufferedReader(path);

        String hostUrl;
        String line = reader.readLine();
        if(line.equalsIgnoreCase("gitlab")){
            hostUrl = reader.readLine();
            String auth = reader.readLine();
            this.projects = readGitlabProjects(hostUrl, auth, reader);
        }
        else if (line.equalsIgnoreCase("github")){
            String auth = reader.readLine();
            this.projects = readGithubProjects(auth, reader);
        }
        else {
            throw new IOException();
        }
    }

    public List<Project> getProjects() {
        return projects;
    }

    private List<Project> readGitlabProjects(String hostUrl, String auth, BufferedReader reader) throws IOException, GitLabApiException {
        List<Project> projects = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] lineWords = line.split("\\s+");
            if(lineWords.length == 2) {
                Project project = new GitlabProject(hostUrl, lineWords[0], lineWords[1], auth);
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
            if(lineWords.length == 2) {
                Project project = new GithubProject(lineWords[0], lineWords[1], auth);
                projects.add(project);
            }
            else {
                throw new IOException();
            }
        }
        return projects;
    }
}