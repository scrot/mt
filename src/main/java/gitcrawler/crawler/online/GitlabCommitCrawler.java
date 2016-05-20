package gitcrawler.crawler.online;

import com.messners.gitlab.api.GitLabApiException;
import com.messners.gitlab.api.models.Diff;
import gitcrawler.api.GitlabAPI;
import gitcrawler.crawler.CommitCrawler;
import gitcrawler.model.Author;
import gitcrawler.model.Commit;
import gitcrawler.model.Project;
import gitcrawler.repository.GLRepoBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/2/16.
 */
public class GitlabCommitCrawler implements CommitCrawler {
    private final GitlabAPI gitlab;
    private final Integer projectID;
    private Map<Object, Commit> commits;

    public GitlabCommitCrawler(Project project) throws GitLabApiException {
        GLRepoBuilder repoBuilder = new GLRepoBuilder(project);
        this.gitlab = repoBuilder.getGitlabApi();
        this.projectID = repoBuilder.getProjectID();
    }

    @Override
    public Map<Object, Commit> getCommits() {
        if(this.commits == null){
            this.commits = collectCommits(gitlab, projectID);
        }
        return this.commits;
    }

    private Map<Object, Commit> collectCommits(GitlabAPI gitlab, Integer projectID) {
        List<com.messners.gitlab.api.models.Commit> glCommits = null;
        try {
            glCommits = gitlab.getCommitsAPI().getCommits(projectID);
            Map<Object, Commit> commits = new HashMap<>();
            for (com.messners.gitlab.api.models.Commit glCommit : glCommits){
                commits.put(glCommit.getId(), new Commit(
                        glCommit.getId(),
                        new Author(""), //new Author(glCommit.getAuthor().getName()), API error
                        glCommit.getMessage(),
                        glCommit.getTimestamp(),
                        getFiles(glCommit)));
            }
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
        return commits;
    }

    private List<Path> getFiles(com.messners.gitlab.api.models.Commit commit) throws GitLabApiException {
        List<Path> files = new ArrayList<>();
        List<Diff> commitDiffs = this.gitlab.getCommitsAPI().getDiffs(this.projectID, commit.getId());
        for(Diff diff : commitDiffs){
            files.add(Paths.get(diff.getNewPath()));
        }
        return files;
    }
}
