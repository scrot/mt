package git.model;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class GitlabCommit extends Commit {

    public GitlabCommit(com.messners.gitlab.api.models.Commit commit, List<Path> commitFiles) {
        super(
                commit.getId(),
                commit.getMessage(),
                commit.getTimestamp(),
                commitFiles,
                Pattern.compile("((?:[Cc]los(?:e[sd]?|ing)|[Ff]ix(?:e[sd]|ing)?) +(?:(?:issues? +)?#\\d+(?:(?:, *| +and +)?))+)")
        );
    }
}
