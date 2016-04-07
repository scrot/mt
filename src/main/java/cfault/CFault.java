package cfault;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class CFault {
    private final Path classPath;
    private final GHCommit relatedCommit;
    private final CommitAction commitAction;
    private final GHIssue relatedIssue;

    public CFault(Path classPath, GHCommit relatedCommit, CommitAction commitAction, GHIssue relatedIssue) throws IOException {
        assert Pattern.compile("#\\d+").matcher(relatedCommit.getCommitShortInfo().getMessage()).find();

        this.classPath = classPath;
        this.relatedCommit = relatedCommit;
        this.commitAction = commitAction;
        this.relatedIssue = relatedIssue;
    }
}
