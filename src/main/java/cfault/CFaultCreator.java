package cfault;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CFaultCreator {
    private final Map<GHCommit, GHIssue> commitIssuesMap;
    private final Map<GHCommit, Path> commitClassMap;

    private final String fixedPattern;
    private final String resolvedPattern;
    private final String closedPattern;

    public CFaultCreator(ProjectRepo projectRepository, Path projectRoot) throws IOException {
        this.fixedPattern = "(F|f)ix(es|ed)?(.*/.*|\\s*)#\\d+";
        this.resolvedPattern = "(R|r)esolve(s|d)?(.*/.*|\\s*)#\\d+";
        this.closedPattern = "(C|c)lose(s|d)?(.*/.*|\\s*)#\\d+";

        List<GHCommit> commits = collectCommits(projectRepository.getRepository());
        Map<Integer, GHIssue> issues = collectIssues(projectRepository.getRepository());
        this.commitIssuesMap = buildCommitIssueMap(commits, issues);
        this.commitClassMap = buildCommitClassPathMap();
    }

    private List<GHCommit> collectCommits(GHRepository repo) {
        return repo.listCommits().asList();
    }

    private Map<GHCommit, GHIssue> buildCommitIssueMap(List<GHCommit> commits, Map<Integer, GHIssue> issues) throws IOException {
        Map<GHCommit, GHIssue> commitIssueMap = new HashMap<>();
        for(GHCommit commit : commits){
            if(containsIssue(commit)){
                Integer issueNumber = extractIssueNumber(commit);
                GHIssue issue = issues.get(issueNumber);
                commitIssueMap.put(commit, issue);
            }
        }
        return commitIssueMap;
    }

    private Map<GHCommit,Path> buildCommitClassPathMap() {
        return null;
    }

    private Map<Integer, GHIssue> collectIssues(GHRepository repository){
        Map<Integer, GHIssue> issueMap = new HashMap<>();
        List<GHIssue> issueList = repository.listIssues(GHIssueState.ALL).asList();
        for(GHIssue issue : issueList){
            issueMap.put(issue.getNumber(), issue);
        }
        return issueMap;
    }

    private Boolean containsIssue(GHCommit commit) throws IOException {
        return issuePattern().matcher(getCommitMessage(commit)).find();
    }

    private Integer extractIssueNumber(GHCommit commit) throws IOException {
        Matcher issueMatcher = issuePattern().matcher(getCommitMessage(commit));
        if(issueMatcher.find()){
            String issueNumber = Pattern.compile("#\\d+").matcher(issueMatcher.group()).group();
            return Integer.parseInt(issueNumber.substring(1));
        }
        return null;
    }

    private Pattern issuePattern(){
        return Pattern.compile(this.fixedPattern + "|" + this.resolvedPattern + "|" + this.closedPattern);
    }

    private String getCommitMessage(GHCommit commit) throws IOException {
        return commit.getCommitShortInfo().getMessage();
    }

    private CommitAction getCommitActionFromMessage(String message) {
        if(Pattern.compile(fixedPattern).matcher(message).find()) {
            return CommitAction.FIX;
        }
        if(Pattern.compile(resolvedPattern).matcher(message).find()) {
            return CommitAction.RESOLVE;
        }
        if(Pattern.compile(closedPattern).matcher(message).find()) {
            return CommitAction.CLOSE;
        }
        else {
            return null;
        }
    }

}
