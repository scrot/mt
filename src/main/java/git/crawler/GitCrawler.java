package git.crawler;

import git.model.Commit;
import git.model.Fault;
import git.model.Issue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GitCrawler {
    protected Map<String, Commit> commits;
    protected Map<Integer, Issue> issues;
    protected Map<Path, List<Fault>> faults;

    protected Map<Path, List<Fault>> collectFaults(){
        Map<Path, List<Fault>> classFaults = new HashMap<>();
        for (Map.Entry<Commit, List<Issue>> commit : buildCommitIssueMap().entrySet()) {
            List<Path> files = commit.getKey().getFiles();
            List<Issue> issues = commit.getValue();

            for (Path file : files){
                for(Issue issue : issues){
                    addValueToList(classFaults, file, new Fault(issue,commit.getKey()));
                }
            }
        }
        return classFaults;
    }

    private Map<Commit, List<Issue>> buildCommitIssueMap() {
        Map<Commit, List<Issue>> commitIssueMap = new HashMap<>();

        for(Commit commit : this.commits.values()){
            for(Integer issueNumber : commit.getIssueNumbers()){
                if(this.issues.containsKey(issueNumber)) {
                    Issue issue = this.issues.get(issueNumber);
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
