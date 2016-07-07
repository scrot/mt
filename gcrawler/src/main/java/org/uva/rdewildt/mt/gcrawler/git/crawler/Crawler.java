package org.uva.rdewildt.mt.gcrawler.git.crawler;

import org.uva.rdewildt.mt.gcrawler.git.model.Author;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;
import org.uva.rdewildt.mt.gcrawler.git.model.Fault;
import org.uva.rdewildt.mt.gcrawler.git.model.Issue;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by roy on 5/5/16.
 */
public abstract class Crawler {

    public abstract Map<String, Set<Fault>> getFaults();

    public abstract Map<String, Set<Author>> getAuthors();

    public abstract Map<String, Set<Commit>> getChanges();

    public abstract Map<Integer, Issue> getIssues();

    protected Map<String, Set<Fault>> collectFaults(Map<String, Set<Commit>> commits) {
        Map<String, Set<Fault>> issueCommits = new HashMap<>();
        for (Map.Entry<String, Set<Commit>> entry : commits.entrySet()) {
            Set<Fault> issueCommit = new HashSet<>();
            for (Commit commit : entry.getValue()) {
                if (commit.containsIssues(faultPattern())) {
                    issueCommit.add(new Fault(null, commit));
                }
            }
            issueCommits.put(entry.getKey(), issueCommit);
        }
        return issueCommits;
    }

    protected Map<String, Set<Author>> collectAuthors(Map<String, Set<Commit>> changes) {
        Map<String, Set<Author>> authors = new HashMap<>();
        for (Map.Entry<String, Set<Commit>> entry : changes.entrySet()) {
            String classname = entry.getKey();
            Collection<Commit> fileChanges = entry.getValue();
            Set<Author> fileAuthors = new HashSet<>();
            for (Commit fileChange : fileChanges) {
                fileAuthors.add(fileChange.getAuthor());
            }
            authors.put(classname, fileAuthors);
        }
        return authors;
    }

    private Pattern faultPattern() {
        return Pattern.compile(
                "(?i)(clos(e[sd]?|ing)|fix(e[sd]|ing)?|resolv(e[sd]?))"
        );
    }

}
