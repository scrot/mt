package org.uva.rdewildt.mt.gcrawler.github;

import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.uva.rdewildt.mt.utils.lang.LanguageFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectCrawler {
    private final List<GhProject> ghProjects;

    public GhProjectCrawler(Integer limit, Map<String, String> filterParameters) {
        this.ghProjects = new ArrayList<>();
        int pages = limit / 100;
        int totalReposCollected = 0;

        RepositoryService service = new RepositoryService(new GhConnector("27ebc848263606096d44116e72df5c9c6493f1f3").getGithub());
        for(int i = 0; i < pages; i++){
            List<SearchRepository> repos = null;
            try {
                repos = service.searchRepositories(filterParameters, i);
                repos.stream().limit(limit).forEach(repo -> ghProjects.add(new GhProject(
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
                        repo.getPushedAt()
                )));
            } catch (IOException ignore) {
                filterParameters.put("stars", "<" + (ghProjects.get(getGhProjects().size() - 1).getStars() - 10));
            }

            System.out.println("Retrieved " + (totalReposCollected += repos != null ? repos.size() : 0) + " repositories");

            if(repos != null && repos.size() < 100){
                break;
            }
        }
    }

    public List<GhProject> getGhProjects() {
        return ghProjects;
    }
}
