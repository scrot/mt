package org.uva.rdewildt.mt.featureset.git.crawler;

import org.uva.rdewildt.mt.featureset.git.model.Author;
import org.uva.rdewildt.mt.featureset.git.model.Commit;
import org.uva.rdewildt.mt.featureset.git.model.Fault;
import org.uva.rdewildt.mt.featureset.git.model.Issue;

import java.util.*;

/**
 * Created by roy on 5/5/16.
 */
public abstract class Crawler {
    private Map<String, Set<Author>> authors;

    public abstract Map<String, Set<Commit>> getCommits();
    public abstract Map<Integer, Issue> getIssues();
    public abstract Map<String, Set<Fault>> getFaults();


    public Map<String, Set<Author>> getAuthors(){
        if(this.authors == null){
            this.authors = collectAuthors(getCommits());
        }
        return this.authors;
    }

    private Map<String, Set<Author>> collectAuthors(Map<String, Set<Commit>> changes){
        Map<String, Set<Author>> authors = new HashMap<>();
        for(Map.Entry<String, Set<Commit>> entry : changes.entrySet()){
            String classname = entry.getKey();
            Collection<Commit> fileChanges = entry.getValue();
            Set<Author> fileAuthors = new HashSet<>();
            for(Commit fileChange : fileChanges){
                fileAuthors.add(fileChange.getAuthor());
            }
            authors.put(classname, fileAuthors);
        }
        return authors;
    }
}
