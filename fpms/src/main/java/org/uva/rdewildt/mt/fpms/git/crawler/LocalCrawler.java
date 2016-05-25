package org.uva.rdewildt.mt.fpms.git.crawler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.uva.rdewildt.mt.fpms.git.model.Author;
import org.uva.rdewildt.mt.fpms.git.model.Commit;
import org.uva.rdewildt.mt.fpms.git.model.Fault;
import org.uva.rdewildt.mt.fpms.git.model.Issue;
import org.uva.rdewildt.mt.xloc.PathCollector;
import org.uva.rdewildt.mt.xloc.lang.Language;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by roy on 5/5/16.
 */
public class LocalCrawler implements ClassCrawler {
    private final ClassCommitCrawler classCommitCrawler;
    private final Map<String, Set<Fault>> faults;
    private final Map<String, Set<Author>> authors;

    public LocalCrawler(Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests, Language ofLanguage) throws Exception {
        Git git = gitFromPath(gitRoot);
        List<Language> lang = new ArrayList<Language>(){{add(ofLanguage);}};
        PathCollector collector = new PathCollector(gitRoot, true, ignoreGenerated, ignoreTests, lang);
        this.classCommitCrawler = new CommitCrawler(git, collector.getFilePaths().get(ofLanguage));

        this.faults = collectFaults(getCommits());
        this.authors = collectAuthors(getCommits());
    }

    @Override
    public Map<String, Set<Commit>> getCommits() {
        return classCommitCrawler.getCommits();
    }

    @Override
    public Map<Integer, Issue> getIssues() {
        return new HashMap<>();
    }

    @Override
    public Map<String, Set<Fault>> getFaults() {
        return this.faults;
    }

    @Override
    public Map<String, Set<Author>> getAuthors() {
        return this.authors;
    }

    private Map<String, Set<Fault>> collectFaults(Map<String, Set<Commit>> commits){
        Map<String, Set<Fault>> issueCommits = new HashMap<>();
        for(Map.Entry<String, Set<Commit>> entry : commits.entrySet()){
            Set<Fault> issueCommit = new HashSet<>();
            for(Commit commit : entry.getValue()){
                if(commit.containsIssues(faultPattern())){
                    issueCommit.add(new Fault(null, commit));
                }
            }
            issueCommits.put(entry.getKey(), issueCommit);
        }
        return issueCommits;
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

    private Git gitFromPath(Path gitPath) throws IOException {
        File gitFolder = Paths.get(gitPath.toString(), ".git").toFile();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.setGitDir(gitFolder)
                .readEnvironment()
                .findGitDir()
                .build();
        return new Git(repo);
    }

    private Pattern faultPattern(){
        return Pattern.compile(
                "(?i)(clos(e[sd]?|ing)|fix(e[sd]|ing)?|resolv(e[sd]?))"
        );
    }
}
