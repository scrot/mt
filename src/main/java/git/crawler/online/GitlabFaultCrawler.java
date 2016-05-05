package git.crawler.online;

import git.crawler.FaultCrawler;
import git.model.Commit;
import git.model.Fault;
import git.model.Issue;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static utils.MapTransformation.addValueToMapList;

/**
 * Created by roy on 5/2/16.
 */
public class GitlabFaultCrawler implements FaultCrawler {
    private final Map<Object, Commit> commits;
    private final Map<Integer, Issue> issues;
    private Map<Path, List<Fault>> faults;

    public GitlabFaultCrawler(Map<Object, Commit> commits, Map<Integer, Issue> issues) {
        this.commits = commits;
        this.issues = issues;
    }

    @Override
    public Map<Path, List<Fault>> getFaults() {
        if(this.faults == null){
            this.faults = collectFaults(commits, issues);
        }
        return this.faults;
    }


    private Map<Path, List<Fault>> collectFaults(Map<Object, Commit> commits, Map<Integer, Issue> issues){
        Map<Path, List<Fault>> classFaults = new HashMap<>();
        for (Map.Entry<Commit, List<Issue>> commit : buildCommitIssueMap(commits, issues).entrySet()) {
            List<Path> files = commit.getKey().getFiles();
            List<Issue> commitIssues = commit.getValue();

            for (Path file : files){
                for(Issue issue : commitIssues){
                    addValueToMapList(classFaults, file, new Fault(issue,commit.getKey()));
                }
            }
        }
        return classFaults;
    }


    private Map<Commit, List<Issue>> buildCommitIssueMap(Map<Object, Commit> commits, Map<Integer, Issue> issues) {
        Map<Commit, List<Issue>> commitIssueMap = new HashMap<>();

        for(Commit commit : commits.values()){
            for(Integer issueNumber : commit.getIssueNumbers(getFaultPattern())){
                if(issues.containsKey(issueNumber)) {
                    Issue issue = issues.get(issueNumber);
                    addValueToMapList(commitIssueMap, commit, issue);
                }
            }
        }
        return commitIssueMap;
    }

    private Pattern getFaultPattern(){
        return Pattern.compile(
                "((?:[Cc]los(?:e[sd]?|ing)|[Ff]ix(?:e[sd]|ing)?) +(?:(?:issues? +)?#\\d+(?:(?:, *| +and +)?))+)"
        );
    }
}
