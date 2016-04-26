package git.model;

import com.messners.gitlab.api.models.Commit;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class SimpleCommit {
    private final String message;
    private final Date date;

    public SimpleCommit(Commit glCommit) {
        this.message = glCommit.getMessage();
        this.date = glCommit.getCommitted_date();
    }

    public SimpleCommit(GHCommit ghCommit) throws IOException {
        this.message = ghCommit.getCommitShortInfo().getMessage();
        this.date = ghCommit.getLastStatus().getCreatedAt();
    }

    public String getMessage() {
        return this.message;
    }

    public Date getDate() {
        return this.date;
    }
}
