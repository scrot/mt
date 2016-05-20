package gitcrawler.api;


import com.messners.gitlab.api.AbstractApi;
import com.messners.gitlab.api.GitLabApi;
import com.messners.gitlab.api.GitLabApiException;
import com.messners.gitlab.api.models.Issue;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class IssuesAPI extends AbstractApi {

    public IssuesAPI(GitLabApi gitLabApi) {
        super(gitLabApi);
    }


    public List<Issue> getIssues (int projectId) throws GitLabApiException {
        List<Issue> issues = new ArrayList<>();

        int page = 0;
        int issuecounter;
        do {
            issuecounter = issues.size();
            Response response = get(Response.Status.OK, null, "projects", projectId, "issues", "?page=" + page, "&per_page=100");
            issues.addAll((response.readEntity(new GenericType<List<Issue>>(){})));
            page++;
        }
        while(issues.size() != issuecounter);

        return issues;
    }

    public Issue getIssue (int projectId, String sha) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "issues", sha);
        return (response.readEntity(Issue.class));
    }

}