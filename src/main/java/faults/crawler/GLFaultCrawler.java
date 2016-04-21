package faults.crawler;

import faults.fault.GLFault;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabCommitDiff;
import org.gitlab.api.models.GitlabIssue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GLFaultCrawler {
    private final GitlabAPI gitlab;
    private final Integer projectID;
    private final Map<GitlabCommit, GitlabIssue> commitIssues;
    private final Map<Path, List<GitlabCommit>> classCommits;

    private final String issuePattern;

    public GLFaultCrawler(GitlabAPI gitlab, Integer projectID, Path projectRoot) throws IOException {
        this.issuePattern = "((?:[Cc]los(?:e[sd]?|ing)|[Ff]ix(?:e[sd]|ing)?) +(?:(?:issues? +)?%{issue_ref}(?:(?:, *| +and +)?))+)";

        this.gitlab = gitlab;
        this.projectID = projectID;

        List<GitlabCommit> issueCommits = collectIssueCommits();
        Map<Integer, GitlabIssue> issues = collectIssues();
        this.commitIssues = buildCommitIssueMap(issueCommits, issues);
        this.classCommits = buildClassCommitMap(issueCommits, projectRoot);
    }

    public Map<Path, List<GLFault>> getClassFaults(){
        Map<Path, List<GLFault>> classFaults = new HashMap<>();
        Set<Path> classPaths = classCommits.keySet();
        for(Path classPath : classPaths){
            List<GLFault> faults = new ArrayList<>();
            for(GitlabCommit commit : classCommits.get(classPath)){
                faults.add(new GLFault(commitIssues.get(commit), commit));
            }
            classFaults.put(classPath, faults);
        }
        return classFaults;
    }

    private List<GitlabCommit> collectIssueCommits() throws IOException {
        List<GitlabCommit> issueCommits = new ArrayList<>();
        List<GitlabCommit> commits = collectCommits();

        int i = 0;
        for(GitlabCommit commit : commits){
            if(containsIssue(commit)){
                issueCommits.add(commit);
            }
            System.out.println("Number of issue commits found " + issueCommits.size() + ", number of commits checked " + i++ + "/" + commits.size());
        }
        return issueCommits;
    }

    private List<GitlabCommit> collectCommits() throws IOException {
        return this.gitlab.getAllCommits(this.projectID);
    }


    private Map<Integer, GitlabIssue> collectIssues() throws IOException {
        Map<Integer, GitlabIssue> issueMap = new HashMap<>();
        List<GitlabIssue> issueList = this.gitlab.getIssues(this.gitlab.getProject(this.projectID));
        for(GitlabIssue issue : issueList){
            issueMap.put(issue.getId(), issue);
        }
        return issueMap;
    }

    private Map<GitlabCommit, GitlabIssue> buildCommitIssueMap(List<GitlabCommit> issueCommits, Map<Integer, GitlabIssue> issues) throws IOException {
        Map<GitlabCommit, GitlabIssue> commitIssueMap = new HashMap<>();

        int i = 0;
        for(GitlabCommit commit : issueCommits){
            Integer issueNumber = extractIssueNumber(commit);
            if(issues.containsKey(issueNumber)) {
                GitlabIssue issue = issues.get(issueNumber);
                commitIssueMap.put(commit, issue);
            }
            System.out.println("Number of issues found " + commitIssueMap.size() + ", number of commits checked" + i++ + "/" + issueCommits.size());
        }
        return commitIssueMap;
    }

    private Map<Path, List<GitlabCommit>> buildClassCommitMap(List<GitlabCommit> commits, Path projectRoot) throws IOException {
        Map<Path, List<GitlabCommit>> classCommitMap = new HashMap<>();
        for(GitlabCommit commit : commits){
            for(String file : getRelativeFilePaths(commit)){
                Path filePath = Paths.get(projectRoot.toString(), file);
                if(!classCommitMap.containsKey(filePath)){
                    classCommitMap.put(filePath, new ArrayList<GitlabCommit>(){{add(commit);}});
                }
                else {
                    List<GitlabCommit> pathCommits = classCommitMap.get(filePath);
                    pathCommits.add(commit);
                    classCommitMap.put(filePath, pathCommits);
                }
            }
        }
        return classCommitMap;
    }

    private List<String> getRelativeFilePaths(GitlabCommit commit) throws IOException {
        List<String> relativeFilePaths = new ArrayList<>();
        List<GitlabCommitDiff> commitDiffs = this.gitlab.getCommitDiffs(projectID, commit.getId());
        for(GitlabCommitDiff diff : commitDiffs){
            relativeFilePaths.add(diff.getNewPath());
        }
        return relativeFilePaths;
    }

    private Boolean containsIssue(GitlabCommit commit) throws IOException {
        return Pattern.compile(this.issuePattern).matcher(getCommitMessage(commit)).find();
    }

    private Integer extractIssueNumber(GitlabCommit commit) throws IOException {
        Matcher issueMatcher = Pattern.compile("#\\d+").matcher(getCommitMessage(commit));
        if(issueMatcher.find()){
            String issueNumber = issueMatcher.group();
            return Integer.parseInt(issueNumber.substring(1));
        }
        return null;
    }

    private String getCommitMessage(GitlabCommit commit) throws IOException {
        return commit.getDescription();
    }
}
