package git.crawler.local;

import git.crawler.FaultCrawler;
import git.model.Commit;
import git.model.Fault;
import git.model.Issue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static utils.MapTransformation.addValueToMapList;

/**
 * Created by roy on 5/5/16.
 */
public class LocalFaultCrawler implements FaultCrawler {
    private final Map<Object, Commit> commits;
    private Map<Path, List<Fault>> faults;

    public LocalFaultCrawler(Map<Object, Commit> commits) {
        this.commits = commits;
    }

    @Override
    public Map<Path, List<Fault>> getFaults() {
        if(this.faults == null){
            this.faults = collectFaults(commits);
        }
        return this.faults;
    }

    private Map<Path, List<Fault>> collectFaults(Map<Object, Commit> commits){
        Map<Path, List<Fault>> classFaults = new HashMap<>();
        for (Commit issueCommit : collectIssueCommits(commits)) {
            List<Path> files = issueCommit.getFiles();

            for (Path file : files){
                    addValueToMapList(classFaults, file, new Fault(null, issueCommit));
            }
        }
        return classFaults;
    }

    private List<Commit> collectIssueCommits(Map<Object, Commit> commits){
        List<Commit> issueCommits = new ArrayList<>();
        for(Commit commit : commits.values()){
            if(commit.containsIssues(getFaultPattern())){
                issueCommits.add(commit);
            }
        }
        return issueCommits;
    }

    private Pattern getFaultPattern(){
        return Pattern.compile(
                "(?i)(clos(e[sd]?|ing)|fix(e[sd]|ing)?|resolv(e[sd]?))"
        );
    }
}
