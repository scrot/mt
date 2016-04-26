package faults.crawler;

import com.messners.gitlab.api.GitLabApiException;
import com.messners.gitlab.api.models.Commit;
import com.messners.gitlab.api.models.Diff;
import com.messners.gitlab.api.models.Issue;
import git.api.GitlabAPI;
import faults.Fault;
import git.model.SimpleCommit;
import git.model.SimpleIssue;
import git.repository.GLRepoBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GLFaultCrawler {
    private final GitlabAPI gitlab;
    private final Integer projectID;
    private final Map<Commit, List<Issue>> commitIssues;
    private final Map<Path, List<Commit>> classCommits;
    private final Map<Path, List<Fault>> classFaults;

    private final String issuePattern;

    public GLFaultCrawler(String domainURL, String group, String project, String token, Path projectRoot) throws GitLabApiException {
        this.issuePattern = "((?:[Cc]los(?:e[sd]?|ing)|[Ff]ix(?:e[sd]|ing)?) +(?:(?:issues? +)?#\\d+(?:(?:, *| +and +)?))+)";
        Pattern.compile(issuePattern);

        GLRepoBuilder glbuilder = new GLRepoBuilder(domainURL, group, project, token);
        this.gitlab = glbuilder.getGitlabApi();
        this.projectID = glbuilder.getProjectID();

        List<Commit> issueCommits = collectIssueCommits();
        Map<Integer, Issue> issues = collectIssues();

        this.commitIssues = buildCommitIssueMap(issueCommits, issues);
        this.classCommits = buildClassCommitMap(issueCommits, projectRoot);
        this.classFaults = buildClassFaults();
    }

    public  Map<Path, List<Fault>> getClassFaults(){
        return this.classFaults;
    }

    private Map<Path, List<Fault>> buildClassFaults(){
        Map<Path, List<Fault>> classFaults = new HashMap<>();
        Set<Path> classPaths = classCommits.keySet();
        for(Path classPath : classPaths){
            List<Fault> faults = new ArrayList<>();
            for(Commit commit : classCommits.get(classPath)){
                for(Issue issue : commitIssues.get(commit)){
                    faults.add(new Fault(
                            new SimpleIssue(issue),
                            new SimpleCommit(commit)));

                }
            }
            classFaults.put(classPath, faults);
        }
        return classFaults;
    }

    private List<Commit> collectIssueCommits() throws GitLabApiException {
        List<Commit> issueCommits = new ArrayList<>();
        List<Commit> commits = collectCommits();

        int i = 0;
        for(Commit commit : commits){
            if(containsIssue(commit)){
                issueCommits.add(commit);
            }
            System.out.println("Number of issue commits found " + issueCommits.size() + ", number of commits checked " + (i++ + 1)  + "/" + commits.size());
        }
        return issueCommits;
    }

    private List<Commit> collectCommits() throws GitLabApiException {
        return this.gitlab.getCommitsAPI().getCommits(this.projectID);
    }


    private Map<Integer, Issue> collectIssues() throws GitLabApiException {
        Map<Integer, Issue> issueMap = new HashMap<>();
        List<Issue> issueList = this.gitlab.getIssuesAPI().getIssues(this.projectID);
        for(Issue issue : issueList){
            issueMap.put(issue.getIid(), issue);
        }
        return issueMap;
    }

    private Map<Commit, List<Issue>> buildCommitIssueMap(List<Commit> issueCommits, Map<Integer, Issue> issues) {
        Map<Commit, List<Issue>> commitIssueMap = new HashMap<>();

        for(Commit commit : issueCommits){
            List<Integer> issueNumbers = extractIssueNumbers(commit);
            for(Integer issueNumber : issueNumbers){
                if(issues.containsKey(issueNumber)) {
                    Issue issue = issues.get(issueNumber);
                    if(!commitIssueMap.containsKey(commit)){
                        commitIssueMap.put(commit, new ArrayList<Issue>(){{add(issue);}});
                    }
                    else {
                        List<Issue> currentIssues = commitIssueMap.get(commit);
                        currentIssues.add(issue);
                        commitIssueMap.put(commit, currentIssues);
                    }
                }
            }
        }
        return commitIssueMap;
    }

    private Map<Path, List<Commit>> buildClassCommitMap(List<Commit> commits, Path projectRoot) throws GitLabApiException {
        Map<Path, List<Commit>> classCommitMap = new HashMap<>();
        for(Commit commit : commits){
            for(String file : getRelativeFilePaths(commit)){
                Path filePath = Paths.get(projectRoot.toString(), file);
                if(!classCommitMap.containsKey(filePath)){
                    classCommitMap.put(filePath, new ArrayList<Commit>(){{add(commit);}});
                }
                else {
                    List<Commit> pathCommits = classCommitMap.get(filePath);
                    pathCommits.add(commit);
                    classCommitMap.put(filePath, pathCommits);
                }
            }
        }
        return classCommitMap;
    }

    private List<String> getRelativeFilePaths(Commit commit) throws GitLabApiException {
        List<String> relativeFilePaths = new ArrayList<>();
        List<Diff> commitDiffs = this.gitlab.getCommitsAPI().getDiffs(this.projectID, commit.getId());
        for(Diff diff : commitDiffs){
            relativeFilePaths.add(diff.getNewPath());
        }
        return relativeFilePaths;
    }

    private Boolean containsIssue(Commit commit) {
        return Pattern.compile(this.issuePattern).matcher(getCommitMessage(commit)).find();
    }

    private List<Integer> extractIssueNumbers(Commit commit) {
        List<Integer> issueNumbers = new ArrayList<>();
        Matcher issueMatcher = Pattern.compile("#\\d+").matcher(getCommitMessage(commit));

        while(issueMatcher.find()){
            String issueNumber = issueMatcher.group();
            Integer issueNr = Integer.parseInt(issueNumber.substring(1));
            if(issueNr != null){
                issueNumbers.add(issueNr);
            }
        }
        return issueNumbers;
    }

    private String getCommitMessage(Commit commit){
        return commit.getMessage();
    }
}
