package report.model;

import git.crawler.GitCrawler;
import git.model.Commit;
import git.model.Fault;
import git.model.Issue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Report {
    private List<Commit> commits;
    private Commit firstCommit;
    private Commit lastCommit;

    private Map<Integer, Issue> issues;
    private Map<Path, List<Fault>> faults;

    public Report(GitCrawler gitCrawler){
        setCommitVars(gitCrawler);
        this.issues = gitCrawler.getIssues();
        this.faults = gitCrawler.getFaults();
    }

    public void setCommitVars(GitCrawler gitCrawler){
        this.commits = new ArrayList<>(gitCrawler.getCommits().values());
        this.firstCommit = this.commits.get(1);
        this.lastCommit = this.commits.get(1);
        for(Commit commit : commits){
            if(commit.getDate().before(this.firstCommit.getDate())){
                this.firstCommit = commit;
            }
            if(commit.getDate().after(this.lastCommit.getDate())){
                this.lastCommit = commit;
            }
        }
    }
}
