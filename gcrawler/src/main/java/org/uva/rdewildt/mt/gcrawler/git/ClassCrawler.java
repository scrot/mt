package org.uva.rdewildt.mt.gcrawler.git;

import org.uva.rdewildt.mt.utils.model.git.Author;
import org.uva.rdewildt.mt.utils.model.git.Commit;
import org.uva.rdewildt.mt.utils.model.git.Fault;
import org.uva.rdewildt.mt.utils.model.git.Issue;
import org.uva.rdewildt.mt.utils.model.lang.Language;
import org.uva.rdewildt.mt.xloc.PathCollector;

import java.nio.file.Path;
import java.util.*;

/**
 * Created by roy on 5/5/16.
 */
public class ClassCrawler extends Crawler {
    private final CommitCrawler commitCrawler;
    private final Map<String, Set<Fault>> faults;
    private final Map<String, Set<Author>> authors;

    public ClassCrawler(Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests, Language ofLanguage) throws Exception {
        List<Language> lang = new ArrayList<Language>() {{
            add(ofLanguage);
        }};
        PathCollector collector = new PathCollector(gitRoot, true, ignoreGenerated, ignoreTests, lang);
        this.commitCrawler = new ClassCommitCrawler(gitRoot, collector.getFilePaths().get(ofLanguage));

        this.faults = collectFaults(getChanges());
        this.authors = collectAuthors(getChanges());
    }

    @Override
    public Map<String, Set<Commit>> getChanges() {
        return commitCrawler.getChanges();
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

}
