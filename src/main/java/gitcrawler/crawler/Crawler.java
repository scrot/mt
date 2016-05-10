package gitcrawler.crawler;

import gitcrawler.model.Author;
import gitcrawler.model.Commit;
import gitcrawler.model.Fault;
import gitcrawler.model.Issue;

import java.nio.file.Path;
import java.util.*;

import static utils.MapTransformation.addValueToMapList;

/**
 * Created by roy on 5/5/16.
 */
public abstract class Crawler {
    private Map<Path, List<Commit>> changes;
    private Map<Path, Set<Author>> authors;

    public abstract Map<Object, Commit> getCommits();
    public abstract Map<Integer, Issue> getIssues();
    public abstract Map<Path, List<Fault>> getFaults();

    public Map<Path, List<Commit>> getChanges(){
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

    private Map<Path, List<Commit>> collectChanges(){
        Map<Path, List<Commit>> changes = new HashMap<>();
        for(Commit commit : this.getCommits().values()){
            List<Path> commitFiles = commit.getFiles();
            for(Path file : commitFiles){
                addValueToMapList(changes, file, commit);
            }
        }
        return changes;
    }

    private Map<Path, Set<Author>> collectAuthors(Map<Path, List<Commit>> changes){
        Map<Path, Set<Author>> authors = new HashMap<>();
        for(Map.Entry<Path, List<Commit>> entry : changes.entrySet()){
            Path path = entry.getKey();
            List<Commit> fileChanges = entry.getValue();
            Set<Author> fileAuthors = new HashSet<>();
            for(Commit fileChange : fileChanges){
                fileAuthors.add(fileChange.getAuthor());
            }
            authors.put(path, fileAuthors);
        }
        return authors;
    }
}
