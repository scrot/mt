package org.uva.rdewildt.mt.gcrawler.github;

import org.apache.maven.cli.MavenCli;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.io.NullOutputStream;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.uva.rdewildt.mt.gcrawler.git.model.Project;
import org.uva.rdewildt.mt.utils.lang.LanguageFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectCrawler {
    private final Set<SearchRepository> ghRepos;
    private final Map<String, GhProject> ghProjects;

    public GhProjectCrawler(Integer limit, Map<String, String> filterParameters) {
        this.ghRepos = new HashSet<>();
        this.ghProjects = new HashMap<>();

        collectRepos(limit, filterParameters);
    }

    public GhProjectCrawler(Integer limit, Map<String, String> filterParameters, Path clonePath, Boolean autoBuild) {
        this.ghRepos = new HashSet<>();
        this.ghProjects = new HashMap<>();

        collectRepos(limit, filterParameters);
        cloneRepos(clonePath);

        if (autoBuild) {
            buildProjects();
        }
    }

    public Set<SearchRepository> getGhRepos() {
        return ghRepos;
    }

    public Map<String, GhProject> getGhProjects() {
        return ghProjects;
    }

    private void collectRepos(Integer limit, Map<String, String> filterParameters) {
        int pages = limit / 100;

        RepositoryService service = new RepositoryService(new GhConnector("27ebc848263606096d44116e72df5c9c6493f1f3").getGithub());
        for (int i = 0; i <= pages; i++) {
            final SearchRepository[] lastElem = {null};
            try {
                List<SearchRepository> repos = service.searchRepositories(filterParameters, i);
                repos.stream().limit(1000).forEach(repo -> {
                    this.ghRepos.add(repo);
                    try {
                        this.ghProjects.put(repo.getOwner() + '-' + repo.getName(), new GhProject(
                                new Project(repo.getUrl(), null, null, repo.getOwner(), repo.getName()),
                                new LanguageFactory().stringToLanguage(repo.getLanguage()),
                                repo.getDescription(),
                                repo.getWatchers(),
                                repo.getForks(),
                                repo.getSize(),
                                repo.getPushedAt(),
                                repo.isHasIssues()));
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    lastElem[0] = repo;
                });
            } catch (IOException ignore) {
                System.out.println("Asking for too many repositories, limit is 1000");
            }

            if (ghProjects.size() >= 1000) {
                filterParameters.put("stars", "<" + (lastElem[0].getWatchers() - 10));
                i = 0;
            }

            System.out.println("Retrieved " + ghProjects.size() + " repositories");

            if (ghProjects.size() >= limit) {
                break;
            }
        }
    }

    private Map<GhProject, Path> cloneRepos(Path clonePath) {
        Map<GhProject, Path> cloned = new HashMap<>();
        this.ghProjects.forEach((k, v) -> {
            try {
                URIish uri = new URIish(v.getProjectUrl());
                //GitUtils.gitClone(uri, clonePath);
                v.setGitRoot(Paths.get(clonePath.toString(), k).toString());
                v.setBinaryRoot(Paths.get(clonePath.toString(), k).toString());
            } catch (URISyntaxException e) {
                System.out.println("invalid uri " + v.getProjectUrl());
            }
        });
        return cloned;
    }

    private void buildProjects() {
        this.ghProjects.forEach((k, v) -> {
            if (isGradlePath(v.getGitRoot())) {
                ProjectConnection gradle = GradleConnector
                        .newConnector()
                        .forProjectDirectory(v.getGitRoot().toFile())
                        .connect();
                try {
                    gradle.newBuild()
                            .forTasks("clean", "build")
                            .setStandardOutput(NullOutputStream.INSTANCE)
                            .run();
                    GhProject update = this.ghProjects.get(k);
                    this.ghProjects.replace(update.getId(), update);
                } catch (Exception e) {
                    System.out.println("Gradle project " + v.getProject() + " could not be build");
                } finally {
                    gradle.close();
                }
            }
            if (isMavenPath(v.getGitRoot())) {
                try {
                    MavenCli cli = new MavenCli();
                    PrintStream devnull = new PrintStream(NullOutputStream.INSTANCE);
                    cli.doMain(new String[]{"clean", "compile"}, Paths.get(v.getGitRoot().toString(), "pom.xml").toString(),
                            devnull, devnull);
                    GhProject update = this.ghProjects.get(k);
                    this.ghProjects.replace(update.getId(), update);
                } catch (Exception e) {
                    System.out.println("maven project " + v.getProject() + " could not be build");
                }
            }
        });
    }

    private Boolean isMavenPath(Path path) {
        return Paths.get(path.toString(), "pom.xml").toFile().exists();
    }

    private Boolean isGradlePath(Path path) {
        return Paths.get(path.toString(), "build.gradle").toFile().exists();
    }
}
