package org.uva.rdewildt.mt.featureset.git.crawler;

import org.uva.rdewildt.mt.featureset.git.model.Commit;
import org.uva.rdewildt.mt.featureset.git.model.Fault;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by roy on 5/5/16.
 */
public class LocalFaultCrawler implements FaultCrawler {
    private Map<String, Set<Fault>> faults;

    public LocalFaultCrawler(Map<String, Set<Commit>> commits) {
        this.faults = filterOnIssueCommits(commits);
    }

    @Override
    public Map<String, Set<Fault>> getFaults() {
        return this.faults;
    }


    private Map<String, Set<Fault>> filterOnIssueCommits(Map<String, Set<Commit>> commits){
        Map<String, Set<Fault>> issueCommits = new HashMap<>();
        for(Map.Entry<String, Set<Commit>> entry : commits.entrySet()){
            Set<Fault> issueCommit = new HashSet<>();
            for(Commit commit : entry.getValue()){
                if(commit.containsIssues(getFaultPattern())){
                    issueCommit.add(new Fault(null, commit));
                }
            }
            issueCommits.put(entry.getKey(), issueCommit);
        }
        return issueCommits;
    }

    private Pattern getFaultPattern(){
        return Pattern.compile(
                "(?i)(clos(e[sd]?|ing)|fix(e[sd]|ing)?|resolv(e[sd]?))"
        );
    }
}
