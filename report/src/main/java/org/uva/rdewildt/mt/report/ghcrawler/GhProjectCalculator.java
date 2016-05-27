package org.uva.rdewildt.mt.report.ghcrawler;

import org.kohsuke.github.*;
import org.uva.rdewildt.mt.xloc.lang.LanguageFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectCalculator {
    private final List<GhProject> ghProjects;

    public GhProjectCalculator(Integer limit, String language, GHRepositorySearchBuilder.Sort sortBy) {
        this.ghProjects = new ArrayList<>();
        GitHub github = new GhConnector().getGithub();

        PagedSearchIterable<GHRepository> repos = github.searchRepositories().language(language).sort(sortBy).list();

        int i = 0;
        while(repos.iterator().hasNext() && i < limit){
            GHRepository repo = repos.iterator().next();
            ghProjects.add(new GhProject(
                tryGetUrl(repo.getSvnUrl()),
                null,
                null,
                repo.getOwnerName(),
                repo.getName(),
                null,
                new LanguageFactory().stringToLanguage(repo.getLanguage()),
                repo.getDescription(),
                Math.toIntExact(repo.listStargazers().spliterator().estimateSize()),
                repo.getForks(),
                getReleaseCount(repo),
                repo.getSubscribersCount(),
                getCollaboratorCount(repo)
            ));
        }
    }

    public List<GhProject> getGhProjects() {
        return ghProjects;
    }

    private Integer getReleaseCount(GHRepository repository){
        int count = 0;
        try{
            count = Math.toIntExact(repository.listReleases().spliterator().estimateSize());
        } catch (IOException ignored) {}
        return count;
    }

    private Integer getCollaboratorCount(GHRepository repository){
        int count = 0;
        try{
            count = Math.toIntExact(repository.listCollaborators().spliterator().estimateSize());
        } catch (IOException ignored) {}
        return count;
    }

    private URL tryGetUrl(String urlString){
        URL url = null;
        try{
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            System.out.println("malformed URL " + url);
        }
        return url;
    }
}
