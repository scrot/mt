package faults.crawler;

import faults.model.Fault;
import faults.model.SimpleCommit;
import faults.model.SimpleIssue;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GHFaultCrawler implements FaultCrawler {
    private final Map<GHCommit, GHIssue> commitIssues;
    private final Map<Path, List<GHCommit>> classCommits;
    private final Map<Path, List<Fault>> classFaults;

    private final String issuePattern;

    public GHFaultCrawler(GHRepository projectRepository, Path projectRoot) throws IOException {
        this.issuePattern = "(?i)(fix(es|ed)?|resolve(s|d)?|close(s|d)?)(.*/.*|\\s*)#\\d+";

        List<GHCommit> issueCommits = collectIssueCommits(projectRepository);
        Map<Integer, GHIssue> issues = collectIssues(projectRepository);

        this.commitIssues = buildCommitIssueMap(issueCommits, issues);
        this.classCommits = buildClassCommitMap(issueCommits, projectRoot);
        this.classFaults = buildClassFaults();
    }

    public  Map<Path, List<Fault>> getClassFaults(){
        return this.classFaults;
    }

    private Map<Path, List<Fault>> buildClassFaults() throws IOException {
        Map<Path, List<Fault>> classFaults = new HashMap<>();
        Set<Path> classPaths = classCommits.keySet();
        for(Path classPath : classPaths){
            List<Fault> faults = new ArrayList<>();
            for(GHCommit commit : classCommits.get(classPath)){
                faults.add(new Fault(
                        new SimpleIssue(commitIssues.get(commit)),
                        new SimpleCommit(commit)));
            }
            classFaults.put(classPath, faults);
        }
        return classFaults;
    }

    private List<GHCommit> collectIssueCommits(GHRepository projectRepository) throws IOException {
        List<GHCommit> issueCommits = new ArrayList<>();
        List<GHCommit> commits = collectCommits(projectRepository);

        int i = 0;
        for(GHCommit commit : commits){
            if(containsIssue(commit)){
                issueCommits.add(commit);
            }
            System.out.println("Number of issue commits found " + issueCommits.size() + ", number of commits checked " + i++ + "/" + commits.size());
        }
        return issueCommits;
    }

    private List<GHCommit> collectCommits(GHRepository repo) {
        return repo.listCommits().asList();
    }


    private Map<Integer, GHIssue> collectIssues(GHRepository repository){
        Map<Integer, GHIssue> issueMap = new HashMap<>();
        List<GHIssue> issueList = repository.listIssues(GHIssueState.ALL).asList();
        for(GHIssue issue : issueList){
            issueMap.put(issue.getNumber(), issue);
        }
        return issueMap;
    }

    private Map<GHCommit, GHIssue> buildCommitIssueMap(List<GHCommit> issueCommits, Map<Integer, GHIssue> issues) throws IOException {
        Map<GHCommit, GHIssue> commitIssueMap = new HashMap<>();

        int i = 0;
        for(GHCommit commit : issueCommits){
            Integer issueNumber = extractIssueNumber(commit);
            if(issues.containsKey(issueNumber)) {
                GHIssue issue = issues.get(issueNumber);
                commitIssueMap.put(commit, issue);
            }
            System.out.println("Number of issues found " + commitIssueMap.size() + ", number of commits checked" + i++ + "/" + issueCommits.size());
        }
        return commitIssueMap;
    }

    private Map<Path, List<GHCommit>> buildClassCommitMap(List<GHCommit> commits, Path projectRoot) throws IOException {
        Map<Path, List<GHCommit>> classCommitMap = new HashMap<>();
        for(GHCommit commit : commits){
            for(GHCommit.File file : commit.getFiles()){
                Path filePath = Paths.get(projectRoot.toString(), file.getFileName());
                if(!classCommitMap.containsKey(filePath)){
                    classCommitMap.put(filePath, new ArrayList<GHCommit>(){{add(commit);}});
                }
                else {
                    List<GHCommit> pathCommits = classCommitMap.get(filePath);
                    pathCommits.add(commit);
                    classCommitMap.put(filePath, pathCommits);
                }
            }
        }
        return classCommitMap;
    }

    private Boolean containsIssue(GHCommit commit) throws IOException {
        return Pattern.compile(this.issuePattern).matcher(getCommitMessage(commit)).find();
    }

    private Integer extractIssueNumber(GHCommit commit) throws IOException {
        Matcher issueMatcher = Pattern.compile("#\\d+").matcher(getCommitMessage(commit));
        if(issueMatcher.find()){
            String issueNumber = issueMatcher.group();
            return Integer.parseInt(issueNumber.substring(1));
        }
        return null;
    }

    private String getCommitMessage(GHCommit commit) throws IOException {
        return commit.getCommitShortInfo().getMessage();
    }
}
