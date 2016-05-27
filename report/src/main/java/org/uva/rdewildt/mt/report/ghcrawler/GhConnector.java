package org.uva.rdewildt.mt.report.ghcrawler;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.OkHttpConnector;

import java.io.IOException;
import java.nio.file.FileSystems;

/**
 * Created by roy on 5/27/16.
 */
public class GhConnector {
    private GitHub github;

    public GhConnector() {
        try {
            this.github = buildGitHubConnector();
        } catch (IOException e) {
            System.out.println(this.getClass().getSimpleName() + " could not connect to github");
        }
    }

    public GhConnector(String authToken) {
        try {
            this.github = buildGitHubConnector(authToken);
        } catch (IOException e) {
            System.out.println(this.getClass().getSimpleName() + " could not connect to github");
        }
    }

    public GitHub getGithub() {
        return github;
    }

    private GitHub buildGitHubConnector() throws IOException {
        String tempdir = System.getProperty("java.io.tmpdir") + "OkHttpCache";
        Cache cache = new Cache(FileSystems.getDefault().getPath(tempdir).toFile(), 50 * 1024 * 1024);
        return GitHubBuilder.fromEnvironment()
                .withConnector(new OkHttpConnector(
                        new OkUrlFactory(new OkHttpClient().setCache(cache))))
                .build();
    }

    private GitHub buildGitHubConnector(String token) throws IOException {
        String tempdir = System.getProperty("java.io.tmpdir") + "OkHttpCache";
        Cache cache = new Cache(FileSystems.getDefault().getPath(tempdir).toFile(), 50 * 1024 * 1024);
        return GitHubBuilder.fromEnvironment()
                .withOAuthToken(token)
                .withConnector(new OkHttpConnector(
                        new OkUrlFactory(new OkHttpClient().setCache(cache))))
                .build();
    }
}
