package org.uva.rdewildt.mt.runner.writer;


import org.uva.rdewildt.mt.gcrawler.git.model.Project;

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

    public ConfigReader(Path path) throws IOException {
        String filename = path.getFileName().toString();
        this.name = filename.substring(0, filename.indexOf('.'));
        BufferedReader reader = Files.newBufferedReader(path);
        this.projects = readGitlabProjects("", "", reader);
    }

    public String getName() {return name; }

    public List<Project> getProjects() {
        return projects;
    }

    private List<Project> readGitlabProjects(String hostUrl, String auth, BufferedReader reader) throws IOException {
        List<Project> projects = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] lineWords = line.split("\\s+");
            if(lineWords.length == 3) {
                Project project = new Project(null, Paths.get(lineWords[1]), Paths.get(lineWords[2]), "", lineWords[0], auth);
                projects.add(project);
            }
            else {
                throw new IOException();
            }
        }
        return projects;
    }
}