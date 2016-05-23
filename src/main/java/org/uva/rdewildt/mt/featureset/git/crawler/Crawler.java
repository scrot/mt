package org.uva.rdewildt.mt.featureset.git.crawler;

import org.uva.rdewildt.mt.featureset.git.model.Author;
import org.uva.rdewildt.mt.featureset.git.model.Commit;
import org.uva.rdewildt.mt.featureset.git.model.Fault;
import org.uva.rdewildt.mt.featureset.git.model.Issue;

import java.nio.file.Path;
import java.util.*;

import static org.uva.rdewildt.mt.utils.Utils.addValueToMapSet;

/**
 * Created by roy on 5/5/16.
 */
public abstract class Crawler {
    private Map<Path, Set<Commit>> changes;
    private Map<Path, Set<Author>> authors;

    public abstract Map<Object, Commit> getCommits();
    public abstract Map<Integer, Issue> getIssues();
    public abstract Map<Path, List<Fault>> getFaults();

    public Map<Path, Set<Commit>> getChanges(){
        if(this.changes == null){
            this.changes = collectChanges();
        }
        return this.changes;
    }

    public Map<Path, Set<Author>> getAuthors(){
        if(this.authors == null){
            this.authors = collectAuthors(getChanges());
        }
        return this.authors;
    }

    private Map<Path, Set<Commit>> collectChanges(){
        Map<Path, Set<Commit>> changes = new HashMap<>();
        for(Commit commit : this.getCommits().values()){
            List<Path> commitFiles = commit.getFiles();
            for(Path file : commitFiles){
                addValueToMapSet(changes, file, commit);
            }
        }
        return changes;
    }

    private Map<Path, Set<Author>> collectAuthors(Map<Path, Set<Commit>> changes){
        Map<Path, Set<Author>> authors = new HashMap<>();
        for(Map.Entry<Path, Set<Commit>> entry : changes.entrySet()){
            Path path = entry.getKey();
            Collection<Commit> fileChanges = entry.getValue();
            Set<Author> fileAuthors = new HashSet<>();
            for(Commit fileChange : fileChanges){
                fileAuthors.add(fileChange.getAuthor());
            }
            authors.put(path, fileAuthors);
        }
        return authors;
    }
}
