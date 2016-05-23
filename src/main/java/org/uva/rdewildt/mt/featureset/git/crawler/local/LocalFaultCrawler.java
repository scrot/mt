package org.uva.rdewildt.mt.featureset.git.crawler.local;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.uva.rdewildt.mt.featureset.SourceVisitor;
import org.uva.rdewildt.mt.featureset.git.crawler.FaultCrawler;
import org.uva.rdewildt.mt.featureset.git.model.Fault;
import org.uva.rdewildt.mt.featureset.model.ClassSource;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by roy on 5/5/16.
 */
public class LocalFaultCrawler implements FaultCrawler {
    private final Git git;
    private Map<Path, List<Fault>> faults;

    public LocalFaultCrawler(Git git, Map<RevCommit, List<Path>> commitPaths) {
        this.git = git;
        Map<RevCommit, List<Path>> issueCommitPaths = filterOnIssueCommits(commitPaths);
    }

    @Override
    public Map<Path, List<Fault>> getFaults() {
        return this.faults;
    }


    private Map<RevCommit, List<Path>> filterOnIssueCommits(Map<RevCommit, List<Path>> commitPaths){
        Map<RevCommit, List<Path>> issueCommitPaths = new HashMap<>();
        for(Map.Entry<RevCommit, List<Path>> entry : commitPaths.entrySet()){
            if(containsIssue(entry.getKey())){
                issueCommitPaths.put(entry.getKey(), entry.getValue());
            }
        }
        return issueCommitPaths;
    }

    private Boolean containsIssue(RevCommit commit){
        if(getFaultPattern().matcher(commit.getFullMessage()).matches()){
            return true;
        }
        else {
            return false;
        }
    }

    private Pattern getFaultPattern(){
        return Pattern.compile(
                "(?i)(clos(e[sd]?|ing)|fix(e[sd]|ing)?|resolv(e[sd]?))"
        );
    }
}
