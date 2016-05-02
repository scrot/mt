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

public class ReportBuilder {
    private final String projectGroup;
    private final String projectName;

    private final Map<Path, XLoc> classesXLoc;

    private final Map<Object, Commit> projectCommits;
    private final Map<Integer, Issue> projectIssues;
    private final Map<Path, List<Fault>> projectFaults;
    //private final Map<Path, List<Commit>> projectChanges;
    //private final Map<Path, Set<Author>> projectAuthors;

    private final Integer projectCommitsCount;
    private final Integer projectIssuesCount;
    private final Integer projectFaultsCount;
    private final Integer projectAgeDaysCount;
    private final Integer projectDevelopmentDaysCount;
    private final Integer projectFilesCount;
    private final Integer projectCodeFilesCount;
    private final Integer projectCodeCount;
    private final Integer projectCommentCount;
    //private final Integer projectChangesCount;
    //private final Integer projectAuthorsCount;

    private final Double codeGini;
    private final Double faultGini;

    private final Distribution codeDistribution;
    private final Distribution faultDistribution;

    public ReportBuilder(Project project) throws IOException, GitLabApiException, GitAPIException {
        GitCrawler gitCrawler = new GitCrawler(project);

        List<Path> projectFiles = new PathsCollector(project.getLocalPath()).collectClassPaths();
        this.classesXLoc = new XLocCalculator(project.getLocalPath()).getResult();
        XLoc totalXLoc = calculateTotalXLoc(this.classesXLoc);

        this.projectGroup = project.getGroup();
        this.projectName = project.getProject();

        this.projectCommits = gitCrawler.getCommits();
        this.projectIssues = gitCrawler.getIssues();
        this.projectFaults = gitCrawler.getFaults();
        //this.projectChanges = project.getGitCrawler().getChanges();

        List<Commit> commitSorted = this.projectCommits.values().stream()
                .sorted(Comparator.comparing(Commit::getDate))
                .collect(Collectors.toList());
        Commit firstCommit = commitSorted.get(0);
        Commit lastCommit = commitSorted.get(commitSorted.size() - 1);

        this.projectCommitsCount = gitCrawler.getCommits().size();
        this.projectIssuesCount = gitCrawler.getIssues().size();
        this.projectFaultsCount = gitCrawler.getFaults().size();
        //this.projectChangesCount = calculateMapListsLengths(this.projectChanges);
        this.projectAgeDaysCount = calculateDateDayDiff(firstCommit.getDate(), new Date());
        this.projectDevelopmentDaysCount = calculateDateDayDiff(firstCommit.getDate(), lastCommit.getDate());
        this.projectFilesCount = projectFiles.size();
        this.projectCodeFilesCount = classesXLoc.size();
        this.projectCodeCount = totalXLoc.getCodeLines();
        this.projectCommentCount = totalXLoc.getCommentLines();
        //this.projectAuthorsCount = calculateTotalUniqueAuthors(this.projectAuthors);


        this.codeDistribution = new Distribution(getCodeCounts(classesXLoc));
        this.codeGini = codeDistribution.giniCoefficient();


        this.faultDistribution = new Distribution(getListCounts(this.projectFaults));
        this.faultGini = faultDistribution.giniCoefficient();
    }

    public Map<String, String> simpleReport(){
        DecimalFormat formatter = new DecimalFormat("#.##");
        Map<String,String> report = new LinkedHashMap<>();
        report.put("Project", this.projectName);
        report.put("TotFiles",this.projectFilesCount.toString());
        report.put("CodeFiles", this.projectCodeFilesCount.toString());
        report.put("CodeLines", this.projectCodeCount.toString());
        report.put("CommLines", this.projectCommentCount.toString());
        report.put("DevDays", this.projectDevelopmentDaysCount.toString());
        report.put("AgeDays", this.projectAgeDaysCount.toString());
        report.put("#Commits", this.projectCommitsCount.toString());
        report.put("#Issues", this.projectIssuesCount.toString());
        report.put("#Faults", this.projectFaultsCount.toString());
        //report.put("#Changes", this.projectChangesCount.toString());
        report.put("FaultDist", "20-" + this.get20Percent(this.faultDistribution));
        report.put("FaultCode", "20-" + this.codeInFiles(this.getMostFaultyFiles(new Percentage(20.0))));
        report.put("FaultGini", formatter.format(this.faultGini));
        report.put("CodeGini", formatter.format(this.codeGini));
        return report;
    }

    private List<FPath> getMostFaultyFiles(Percentage percentage){
        List<FPath> faulty = new ArrayList<>();
        for(Map.Entry<Path, List<Fault>> entry : this.projectFaults.entrySet()){
            faulty.add(new FPath(entry.getKey(), entry.getValue().size()));
        }
        faulty.sort(Comparator.comparing(FPath::getFaultCount).reversed());
        return faulty.subList(0, (int)(this.projectFaults.size() * percentage.getPercentage0to1()));
    }

    private Integer codeInFiles(List<FPath> files){
        int cloc = 0;
        for(FPath file : files){
            cloc += this.classesXLoc.get(file.getPath()).getCodeLines();
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
