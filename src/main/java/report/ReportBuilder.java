package report;

import com.messners.gitlab.api.GitLabApiException;
import distr.Distribution;
import distr.PathsCollector;
import distr.Percentage;
import git.crawler.DefaultCrawler;
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

public class ReportBuilder {
    //private final Integer projectId;
    private final String projectGroup;
    private final String projectName;

    private final Map<String, Commit> projectCommits;
    private final Map<Integer, Issue> projectIssues;
    private final Map<Path, List<Fault>> projectFaults;
    //private final Map<Path, List<Commit>> projectChanges;
    //private final Map<Path, Set<Author>> projectAuthors;

    private final Integer projectCommitsCount;
    private final Integer projectIssuesCount;
    private final Integer projectFaultsCount;
    //private final Integer projectAgeDaysCount;
    //private final Integer projectDevelopmentDaysCount;
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
        DefaultCrawler crawler = new DefaultCrawler(project);
        List<Path> projectFiles = new PathsCollector(project.getLocalPath()).collectClassPaths();
        Map<Path, XLoc> classesXLoc = new XLocCalculator(project.getLocalPath()).getResult();
        XLoc totalXLoc = calculateTotalXLoc(classesXLoc);

        //this.projectId = project.getId();
        this.projectGroup = project.getGroup();
        this.projectName = project.getProject();

        this.projectCommits = crawler.getCommits();
        this.projectIssues = crawler.getIssues();
        this.projectFaults = crawler.getFaults();
        //this.projectChanges = project.getGitCrawler().getChanges();


        this.projectCommitsCount = crawler.getCommits().size();
        this.projectIssuesCount = crawler.getIssues().size();
        this.projectFaultsCount = crawler.getFaults().size();
        //this.projectChangesCount = calculateMapListsLengths(this.projectChanges);
        //this.projectAgeDaysCount = calculateDateDayDiff(project.getGitCrawler().createdAt(), new Date());
        //this.projectDevelopmentDaysCount = calculateDateDayDiff(project.getGitCrawler().createdAt(), project.getGitCrawler().lastModified());
        this.projectFilesCount = projectFiles.size();
        this.projectCodeFilesCount = classesXLoc.size();
        this.projectCodeCount = totalXLoc.getCodeLines();
        this.projectCommentCount = totalXLoc.getCommentLines();
        //this.projectAuthorsCount = calculateTotalUniqueAuthors(this.projectAuthors);


        this.codeDistribution = new Distribution(getCodeCounts(classesXLoc));
        this.codeGini = codeDistribution.giniCoefficient();


        this.faultDistribution = new Distribution(getListCounts(this.getProjectFaults()));
        this.faultGini = faultDistribution.giniCoefficient();
    }

    public Integer getProjectId() {
        return 0;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public String getProjectName() {
        return projectName;
    }

    public Map<String, Commit> getProjectCommits() {
        return projectCommits;
    }

    public Map<Integer, Issue> getProjectIssues() {
        return projectIssues;
    }

    public Map<Path, List<Fault>> getProjectFaults() {
        return projectFaults;
    }

    public Map<Path, List<Commit>> getProjectChanges() {
        return null;
    }

    public Map<Path, Set<Author>> getProjectAuthors() {
        return null;
    }

    public Integer getProjectCommitsCount() {
        return projectCommitsCount;
    }

    public Integer getProjectIssuesCount() {
        return projectIssuesCount;
    }

    public Integer getProjectFaultsCount() {
        return projectFaultsCount;
    }

    public Integer getProjectAuthorsCount() {
        return 0;
    }

    public Integer getProjectChangesCount() {
        return 0;
    }

    public Integer getProjectAgeDaysCount() {
        return 0;
    }

    public Integer getProjectDevelopmentDaysCount() {
        return 0;
    }

    public Integer getProjectFilesCount() {
        return projectFilesCount;
    }

    public Integer getProjectCodeFilesCount() { return projectCodeFilesCount; }

    public Integer getProjectCodeCount() {
        return projectCodeCount;
    }

    public Integer getProjectCommentCount() {
        return projectCommentCount;
    }

    public Double getCodeGini() {
        return codeGini;
    }

    public Double getFaultGini() {
        return faultGini;
    }

    public Double getPercentageCodeInPartition(Percentage percentage){
        return this.codeDistribution.cumulativeOfPartitionPercentage(percentage).getPercentage();
    }

    public Double getPercentageFaultInPartition(Percentage percentage){
        return this.faultDistribution.cumulativeOfPartitionPercentage(percentage).getPercentage();
    }

    public Map<String, String> simpleReport(){
        DecimalFormat formatter = new DecimalFormat("#.##");
        return new LinkedHashMap<String, String>(){{
            put("Project", getProjectId().toString());
            put("TotFiles", getProjectFilesCount().toString());
            put("CodeFiles", getProjectCodeFilesCount().toString());
            put("CodeLines", getProjectCodeCount().toString());
            put("CommLines", getProjectCommentCount().toString());
            put("DevDays", getProjectDevelopmentDaysCount().toString());
            put("AgeDays", getProjectAgeDaysCount().toString());
            put("#Commits", getProjectCommitsCount().toString());
            put("#Issues", getProjectIssuesCount().toString());
            put("#Faults", getProjectFaultsCount().toString());
            put("#Changes", getProjectChangesCount().toString());
            put("FaultDist", "20-" + getPercentageFaultInPartition(new Percentage(20.0)).intValue());
            put("CodeDist", "20-" +getPercentageCodeInPartition(new Percentage(20.0)).intValue());
            put("FaultGini", formatter.format(getFaultGini()));
            put("CodeGini", formatter.format(getCodeGini()));
        }};
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
