package org.uva.rdewildt.mt.gcrawler.git.crawler;

import org.eclipse.jgit.api.Git;
import org.uva.rdewildt.mt.gcrawler.git.GitUtils;
import org.uva.rdewildt.mt.gcrawler.git.model.Author;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;
import org.uva.rdewildt.mt.gcrawler.git.model.Fault;
import org.uva.rdewildt.mt.gcrawler.git.model.Issue;
import org.uva.rdewildt.mt.xloc.PathCollector;
import org.uva.rdewildt.mt.utils.lang.Language;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by roy on 5/26/16.
 */
public class FLocalCrawler extends Crawler {
    private final CommitCrawler commitCrawler;
    private final Map<String, Set<Fault>> faults;
    private final Map<String, Set<Author>> authors;

    public FLocalCrawler(Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests, Boolean usePathNames, Language ofLanguage) throws IOException {
        try (Git git = GitUtils.gitFromPath(gitRoot)) {
            List<Language> lang = new ArrayList<Language>() {{
                add(ofLanguage);
            }};
            PathCollector collector = new PathCollector(gitRoot, true, ignoreGenerated, ignoreTests, lang);
            this.commitCrawler = new FCommitCrawler(git, gitRoot, usePathNames, collector.getFilePaths().get(ofLanguage));

            this.faults = collectFaults(getChanges());
            this.authors = collectAuthors(getChanges());
        }
    }

    @Override
    public Map<String, Set<Commit>> getChanges() {
        return this.commitCrawler.getChanges();
    }

    @Override
    public Map<String, Set<Fault>> getFaults() {
        return this.faults;
    }

    @Override
    public Map<String, Set<Author>> getAuthors() {
        return this.authors;
    }

    @Override
    public Map<Integer, Issue> getIssues() {
        return new HashMap<>();
    }
}
