package report;

import com.messners.gitlab.api.GitLabApiException;
import distr.Distribution;
import distr.PathsCollector;
import distr.Percentage;
import git.crawler.GitCrawler;
import git.model.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.joda.time.DateTime;
import org.joda.time.Days;
import xloc.XLoc;
import xloc.XLocCalculator;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class OverviewBuilder {
    private final String projectGroup;
    private final String projectName;

    private final Map<Object, Commit> commits;
    private final Map<Object, Commit> codeCommits;
    private final Integer commitsCount;
    private final Integer codeCommitsCount;

    private final Map<Integer, Issue> issues;
    private final Map<Integer, Issue> codeIssues;
    private final Integer issuesCount;
    private final Integer codeIssuesCount;

    private final Map<Path, List<Fault>> faults;
    private final Map<Path, List<Fault>> codeFaults;
    private final Integer faultsCount;
    private final Integer codeFaultsCount;

    //private final Map<Path, List<Commit>> projectChanges;
    //private final Map<Path, Set<Author>> projectAuthors;

    private final Integer ageDaysCount;
    private final Integer developmentDaysCount;
    private final Integer filesCount;
    private final Integer codeFilesCount;
    private final Integer codeCount;
    private final Integer commentCount;
    //private final Integer projectChangesCount;
    //private final Integer projectAuthorsCount;


    private final Distribution codeDistribution;
    private final Integer codeIn20Faults;
    private final Double codeGini;

    private final Distribution faultDistribution;
    private final Integer faultsIn20;
    private final Double faultGini;

    public OverviewBuilder(Project project) throws IOException, GitLabApiException, GitAPIException {
        GitCrawler gitCrawler = new GitCrawler(project);

        List<Path> projectFiles = new PathsCollector(project.getLocalPath()).collectClassPaths();
        Map<Path, XLoc> classesXLoc = new XLocCalculator(project.getLocalPath()).getResult();
        XLoc totalXLoc = calculateTotalXLoc(classesXLoc);
        Map<Path, List<Fault>> codeFaults = filterContains(gitCrawler.getFaults(), classesXLoc);

        this.projectGroup = project.getGroup();
        this.projectName = project.getProject();

        this.filesCount = projectFiles.size();
        this.codeFilesCount = classesXLoc.size();

        this.commits = gitCrawler.getCommits();
        this.commitsCount = gitCrawler.getCommits().size();
        this.codeCommits = extractCommitsFromFaults(codeFaults);
        this.codeCommitsCount = this.codeCommits.size();

        this.issues = gitCrawler.getIssues();
        this.codeIssues = extractIssuesFromFaults(codeFaults);
        this.issuesCount = this.issues.size();
        this.codeIssuesCount = this.codeIssues.size();


        this.faults = gitCrawler.getFaults();
        this.codeFaults = codeFaults;
        this.faultsCount = gitCrawler.getFaults().size();
        this.codeFaultsCount = this.codeFaults.size();
        //this.projectChanges = project.getGitCrawler().getChanges();

        List<Commit> commitSorted = sortCommits(this.commits);
        Commit firstCommit = commitSorted.get(0);
        Commit lastCommit = commitSorted.get(commitSorted.size() - 1);

        //this.projectChangesCount = calculateMapListsLengths(this.projectChanges);
        this.ageDaysCount = calculateDateDayDiff(firstCommit.getDate(), new Date());
        this.developmentDaysCount = calculateDateDayDiff(firstCommit.getDate(), lastCommit.getDate());
        this.codeCount = totalXLoc.getCodeLines();
        this.commentCount = totalXLoc.getCommentLines();
        //this.projectAuthorsCount = calculateTotalUniqueAuthors(this.projectAuthors);


        this.codeDistribution = new Distribution(getCodeCounts(classesXLoc));
        this.codeIn20Faults = getCodeIn20Percent(codeFaults, classesXLoc);
        this.codeGini = codeDistribution.giniCoefficient();

        this.faultDistribution = new Distribution(getListCounts(this.faults));
        this.faultsIn20 = this.get20Percent(this.faultDistribution);
        this.faultGini = faultDistribution.giniCoefficient();
    }

    public Map<String, String> simpleReport(){
        DecimalFormat formatter = new DecimalFormat("#.##");
        Map<String,String> report = new LinkedHashMap<>();
        report.put("Project", this.projectName);
        report.put("TotFiles",this.filesCount.toString());
        report.put("CodeFiles", this.codeFilesCount.toString());
        report.put("CodeLines", this.codeCount.toString());
        report.put("CommLines", this.commentCount.toString());
        report.put("DevDays", this.developmentDaysCount.toString());
        report.put("AgeDays", this.ageDaysCount.toString());
        report.put("TotCommits", this.commitsCount.toString());
        report.put("CodeCommits", this.codeCommitsCount.toString());
        report.put("TotIssues", this.issuesCount.toString());
        report.put("CodeIssues", this.codeIssuesCount.toString());
        report.put("TotFaults", this.faultsCount.toString());
        report.put("CodeFaults", this.codeFaultsCount.toString());
        report.put("FaultDist", "20-" + this.faultsIn20);
        report.put("FaultCode", "20-" + this.codeIn20Faults);
        report.put("FaultGini", formatter.format(this.faultGini));
        report.put("CodeGini", formatter.format(this.codeGini));
        return report;
    }

    private Integer getCodeIn20Percent(Map<Path, List<Fault>> codeFaults, Map<Path, XLoc> clocs){
        List<FPath> faulty = getMostFaultyFiles(codeFaults, new Percentage(20.0));
        Integer faulyCloc = codeInFiles(faulty, clocs);
        return new Percentage(faulyCloc / this.codeCount.doubleValue() * 100).getPercentage().intValue();
    }

    private List<FPath> getMostFaultyFiles(Map<Path, List<Fault>> codeFaults, Percentage percentage){
        List<FPath> faulty = new ArrayList<>();
        for(Map.Entry<Path, List<Fault>> entry : codeFaults.entrySet()){
            faulty.add(new FPath(entry.getKey(), entry.getValue().size()));
        }
        faulty.sort(Comparator.comparing(FPath::getFaultCount).reversed());
        return faulty.subList(0, (int)(this.codeFaults.size() * percentage.getPercentage0to1()));
    }

    private Integer codeInFiles(List<FPath> files, Map<Path,XLoc> locs){
        int cloc = 0;
        for(FPath file : files){
            cloc += locs.get(file.getPath()).getCodeLines();
        }
        return cloc;
    }

    private Integer get20Percent(Distribution d){
        return d.cumulativeOfPartitionPercentage(new Percentage(20.0)).getPercentage().intValue();
    }

    private <T> Map<T, Integer> getCodeCounts(Map<T, XLoc> values){
        Map<T, Integer> counts = new HashMap<>();

        for(Map.Entry<T, XLoc> entry : values.entrySet()){
            counts.put(entry.getKey(), entry.getValue().getCodeLines());
        }

        return counts;
    }

    private <T,U,V> Map<T,U> filterContains(Map<T,U> toFilter, Map<T,V> toCheck){
        Map<T,U> result = new HashMap<>();
        for(Map.Entry<T,U> entry : toFilter.entrySet()){
            if(toCheck.containsKey(entry.getKey())){
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private List<Commit> sortCommits(Map<Object, Commit> commits) {
        return commits.values().stream()
                .sorted(Comparator.comparing(Commit::getDate))
                .collect(Collectors.toList());
    }

    private <T,U> Map<T, Integer> getListCounts(Map<T, List<U>> values){
        Map<T, Integer> counts = new HashMap<>();

        for(Map.Entry<T, List<U>> entry : values.entrySet()){
            counts.put(entry.getKey(), entry.getValue().size());
        }

        return counts;
    }

    private Integer calculateDateDayDiff(Date start, Date end) {
        DateTime startt = new DateTime(start);
        DateTime endt = new DateTime(end);
        Days d = Days.daysBetween(startt, endt);
        return d.getDays();
    }

    private Map<Integer, Issue> extractIssuesFromFaults(Map<Path, List<Fault>> faults) {
        Map<Integer, Issue> filteredIssues = new HashMap<>();
        for(List<Fault> entry : faults.values()){
            for(Fault fault : entry){
                filteredIssues.put(fault.getIssue().getId(), fault.getIssue());
            }
        }
        return filteredIssues;
    }

    private Map<Object, Commit> extractCommitsFromFaults(Map<Path, List<Fault>> faults) {
        Map<Object, Commit> filteredCommits = new HashMap<>();
        for(List<Fault> entry : faults.values()){
            for(Fault fault : entry){
                filteredCommits.put(fault.getCommit().getId(), fault.getCommit());
            }
        }
        return filteredCommits;
    }

    private Integer calculateTotalUniqueAuthors(Map<Path, Set<Author>> authors){
        Set<Author> uniqueAuthors = new HashSet<>();
        for(Map.Entry<Path, Set<Author>> entry : authors.entrySet()){
            uniqueAuthors.addAll(entry.getValue());
        }
        return uniqueAuthors.size();
    }

    private <K,V> Integer calculateMapListsLengths(Map<K,? extends Collection<V>> map){
        Integer counter = 0;
        for(Collection<V> value : map.values()){
            counter += value.size();
        }
        return counter;
    }

    private XLoc calculateTotalXLoc(Map<Path, XLoc> classesXLoc) {
        XLoc totalXLoc = new XLoc(0,0,0,0);
        for(Map.Entry<Path, XLoc> entry : classesXLoc.entrySet()){
            totalXLoc = totalXLoc.add(entry.getValue());
        }
        return totalXLoc;
    }


}
