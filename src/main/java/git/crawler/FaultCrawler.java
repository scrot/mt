package git.crawler;

import com.messners.gitlab.api.GitLabApiException;
import git.model.Fault;
import git.model.Commit;
import git.model.SimpleIssue;

import java.nio.file.Path;
import java.util.*;

public class FaultCrawler {
    private final GitCrawler gitCrawler;
    private final Map<Path, List<Fault>> classFaults;

    public FaultCrawler(GitCrawler gitCrawler) throws GitLabApiException {
        this.gitCrawler = gitCrawler;
        this.classFaults = buildClassFaults();
    }

    public  Map<Path, List<Fault>> getClassFaults(){
        return this.classFaults;
    }

    private Map<Path, List<Fault>> buildClassFaults(){
        Map<Path, List<Fault>> classFaults = new HashMap<>();
        for (Map.Entry<Commit, List<SimpleIssue>> commit : buildCommitIssueMap().entrySet()) {
            List<Path> files = commit.getKey().getFiles();
            List<SimpleIssue> issues = commit.getValue();

            for (Path file : files){
                for(SimpleIssue issue : issues){
                    addValueToList(classFaults, file, new Fault(issue,commit.getKey()));
                }
            }
        }
        return classFaults;
    }

    private Map<Commit, List<SimpleIssue>> buildCommitIssueMap() {
        Map<Commit, List<SimpleIssue>> commitIssueMap = new HashMap<>();

        for(Commit commit : this.gitCrawler.getCommits().values()){
            for(Integer issueNumber : commit.getIssueNumbers()){
                if(this.gitCrawler.getIssues().containsKey(issueNumber)) {
                    SimpleIssue issue = this.gitCrawler.getIssues().get(issueNumber);
                    addValueToList(commitIssueMap, commit, issue);
                }
            }
        }
        return commitIssueMap;
    }

    private <K, V> void addValueToList(Map<K, List<V>> map, K key, V value){
        if(!map.containsKey(key)){
            map.put(key, new ArrayList<V>(){{add(value);}});
        }

        else {
            List<V> currentIssues = map.get(key);
            currentIssues.add(value);
            map.put(key, currentIssues);
        }
    }
}
