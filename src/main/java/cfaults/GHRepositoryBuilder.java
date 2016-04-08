package cfaults;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.regex.Pattern;

public class GHRepositoryBuilder {
    private final GHRepository repository;

    public GHRepositoryBuilder(String repositoryName) throws IOException {
        GitHub github;
        try {
            github = GitHub.connect();
        } catch (IOException e) {
            github = GitHub.connectAnonymously();
        }
        this.repository = github.getRepository(repositoryName);
    }

    public GHRepositoryBuilder(String repositoryName, String OauthToken) throws IOException {
        GitHub github = GitHub.connectUsingOAuth(OauthToken);
        this.repository = github.getRepository(repositoryName);
    }

    public GHRepositoryBuilder(String repositoryName, String userName, String userPassword) throws IOException {
        assert Pattern.compile(".*/.*").matcher(repositoryName).matches();
        GitHub github = GitHub.connectUsingPassword(userName, userPassword);
        this.repository = github.getRepository(repositoryName);
    }

    public GHRepository getRepository() {
        return repository;
    }
}
