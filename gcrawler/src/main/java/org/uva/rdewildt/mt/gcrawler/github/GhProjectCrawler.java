package org.uva.rdewildt.mt.gcrawler.github;

import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.uva.rdewildt.mt.utils.lang.LanguageFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectCrawler {
    private final Set<GhProject> ghProjects;

    public GhProjectCrawler(Integer limit, Map<String, String> filterParameters) {
        this.ghProjects = new LinkedHashSet<>();
        int pages = limit / 100;
        int totalReposCollected = 0;

        RepositoryService service = new RepositoryService(new GhConnector("27ebc848263606096d44116e72df5c9c6493f1f3").getGithub());
        for(int i = 0; i < pages; i++){
            final SearchRepository[] lastElem = {null};
            List<SearchRepository> repos = null;
            try {
                repos = service.searchRepositories(filterParameters, i);
                repos.stream().limit(1000).forEach(repo -> {
                    ghProjects.add(new GhProject(
                        repo.getUrl(),
                        null,
                        null,
                        repo.getOwner(),
                        repo.getName(),
                        null,
                        new LanguageFactory().stringToLanguage(repo.getLanguage()),
                        repo.getDescription(),
                        repo.getWatchers(),
                        repo.getForks(),
                        repo.getSize(),
                        repo.getPushedAt()));
                    lastElem[0] = repo;
                });
            } catch (IOException ignore) {
                System.out.println("Asking for too many repositories, limit is 1000");
            }

            if(ghProjects.size() >= 1000){
                filterParameters.put("stars", "<" + (lastElem[0].getWatchers() - 10));
                i = 0;
            }

            System.out.println("Retrieved " + (totalReposCollected += repos != null ? repos.size() : 0) + " repositories");

            if(repos != null && ghProjects.size() >= limit){
                break;
            }
        }
    }

    public Set<GhProject> getGhProjects() {
        return ghProjects;
    }
}
