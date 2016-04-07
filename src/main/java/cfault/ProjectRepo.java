package cfault;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.regex.Pattern;

public class ProjectRepo {
    private final GHRepository repository;

    public ProjectRepo(String repositoryUser, String repositoryName) throws IOException {
        GitHub github;
        try {
            github = GitHub.connect();
        } catch (IOException e) {
            github = GitHub.connectAnonymously();
        }
        this.repository = github.getRepository(repositoryUser + "/" + repositoryName);
    }

    public ProjectRepo(String repositoryUser, String repositoryName, String OauthToken) throws IOException {
        assert Pattern.compile(".*/.*").matcher(repositoryName).matches();
        GitHub github = GitHub.connectUsingOAuth(OauthToken);
        this.repository = github.getRepository(repositoryUser + "/" + repositoryName);
    }

    public ProjectRepo(String repositoryUser, String repositoryName, String userName, String userPassword) throws IOException {
        assert Pattern.compile(".*/.*").matcher(repositoryName).matches();
        GitHub github = GitHub.connectUsingPassword(userName, userPassword);
        this.repository = github.getRepository(repositoryUser + "/" + repositoryName);
    }

    public GHRepository getRepository() {
        return repository;
    }
}
