package org.uva.rdewildt.mt.report.ghcrawler;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.uva.rdewildt.mt.fpms.git.model.Project;

import java.io.IOException;

/**
 * Created by roy on 5/27/16.
 */
public class GhRepoBuilder {
    private GHRepository repository;

    public GhRepoBuilder(Project project) {
        try {
            GitHub gitHub = new GhConnector(project.getAuthToken()).getGithub();
            this.repository = gitHub.getRepository(project.getGroup() + '/' + project.getProject());
        } catch (IOException e) {
            System.out.println(this.getClass().getSimpleName() + " Could not receive repository " + project.getGroup() + '/' + project.getProject());
        }
    }

    public GhRepoBuilder(String repositoryName, String oAuthToken) {
        try {
            GitHub gitHub = new GhConnector(oAuthToken).getGithub();
            this.repository = gitHub.getRepository(repositoryName);
        } catch (IOException e) {
            System.out.println(this.getClass().getSimpleName() + " Could not receive repository " + repositoryName);
        }
    }


    public GHRepository getRepository() {
        return this.repository;
    }
}
