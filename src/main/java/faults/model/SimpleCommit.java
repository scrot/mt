package faults.model;

import com.messners.gitlab.api.models.Commit;
import org.kohsuke.github.GHCommit;

import java.io.IOException;

public class SimpleCommit {
    private final String message;

    public SimpleCommit(Commit glCommit) {
        this.message = glCommit.getMessage();
    }

    public SimpleCommit(GHCommit ghCommit) throws IOException {
        this.message = ghCommit.getCommitShortInfo().getMessage();
    }

    public String getMessage() {
        return message;
    }
}
