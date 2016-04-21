package faults.repository;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.OkHttpConnector;

import java.io.IOException;
import java.nio.file.FileSystems;

public class GHRepoBuilder implements RepoBuilder {
    private final GitHub gitHub;
    private final GHRepository repository;

    public GHRepoBuilder(String repositoryName, String oAuthToken) throws IOException {
        this.gitHub = buildGitHubConnector(oAuthToken);
        this.repository = this.gitHub.getRepository(repositoryName);
    }

    private GitHub buildGitHubConnector(String token) throws IOException {
        String tempdir = System.getProperty("java.io.tmpdir") + "OkHttpCache";
        Cache cache = new Cache(FileSystems.getDefault().getPath(tempdir).toFile(), 10 * 1024 * 1024); // 10MB cache
        return GitHubBuilder.fromEnvironment()
                .withOAuthToken(token)
                .withConnector(new OkHttpConnector(
                        new OkUrlFactory(new OkHttpClient().setCache(cache))))
                .build();
    }

    public GHRepository getRepository() {
        return this.repository;
    }
}
