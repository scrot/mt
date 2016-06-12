package org.uva.rdewildt.mt.gcrawler.github;

import org.eclipse.egit.github.core.client.GitHubClient;

/**
 * Created by roy on 5/27/16.
 */
public class GhConnector {
    private GitHubClient github;

    public GhConnector() {
        this.github = new GitHubClient();
    }

    public GhConnector(String authToken) {
        this.github = new GitHubClient();
        this.github.setOAuth2Token(authToken);
    }

    public GitHubClient getGithub() {
        return github;
    }

}
