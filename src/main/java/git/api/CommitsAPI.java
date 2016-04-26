package git.api;

import com.messners.gitlab.api.CommitsApi;
import com.messners.gitlab.api.GitLabApi;
import com.messners.gitlab.api.GitLabApiException;
import com.messners.gitlab.api.models.Commit;
import com.messners.gitlab.api.models.Diff;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class CommitsAPI extends CommitsApi {
    public CommitsAPI(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    @Override
    public List<Commit> getCommits(int projectId) throws GitLabApiException {
        List<Commit> commits = new ArrayList<>();

        int page = 0;
        int commitcounter;
        do {
            commitcounter = commits.size();
            Response response = get(Response.Status.OK, null, "projects", projectId, "repository", "commits", "?page=" + page, "&per_page=100");
            commits.addAll((response.readEntity(new GenericType<List<Commit>>(){})));
            page++;
        }
        while(commits.size() != commitcounter);

        return commits;
    }

    public List<Diff> getDiffs (int projectId, String sha) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "repository", "commits", sha, "diff");
        return response.readEntity(new GenericType<List<Diff>>(){});
    }

}
