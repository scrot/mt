package report;

import com.messners.gitlab.api.GitLabApiException;
import distr.CodeDistribution;
import distr.FaultDistribution;
import distr.PathsCollector;
import git.model.*;
import org.joda.time.DateTime;
import org.joda.time.Days;
import xloc.XLoc;
import xloc.XLocCalculator;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

public class ReportBuilder {
    private final Map<String, Commit> projectCommits;
    private final Map<Integer, Issue> projectIssues;
    private final Map<Path, List<Fault>> projectFaults;
    private final Map<Path, List<Commit>> projectChanges;
    private final Map<Path, Set<Author>> projectAuthors;

    private final Integer projectCommitsCount;
    private final Integer projectIssuesCount;
    private final Integer projectFaultsCount;
    private final Integer projectAgeDaysCount;
    private final Integer projectDevelopmentDaysCount;
    private final Integer projectFilesCount;
    private final Integer projectCodeCount;
    private final Integer projectCommentCount;
    private final Integer projectChangesCount;
    private final Integer projectAuthorsCount;

    private final CodeDistribution codeDistribution;
    private FaultDistribution faultDistribution;

    public ReportBuilder(Project project, Path projectPath) throws IOException, GitLabApiException {
        List<Path> projectFiles = new PathsCollector(projectPath).collectClassPaths();
        Map<Path, XLoc> classesXLoc = new XLocCalculator(projectPath).getResult();
        XLoc totalXLoc = calculateTotalXLoc(classesXLoc);

        this.projectCommits = project.getGitCrawler().getCommits();
        this.projectIssues = project.getGitCrawler().getIssues();
        this.projectFaults = project.getGitCrawler().getFaults();
        this.projectChanges = project.getGitCrawler().getChanges();
        this.projectAuthors = project.getGitCrawler().getAuthors();

        this.projectCommitsCount = project.getGitCrawler().getCommits().size();
        this.projectIssuesCount = project.getGitCrawler().getIssues().size();
        this.projectFaultsCount = project.getGitCrawler().getFaults().size();
        this.projectChangesCount = calculateMapListsLengths(this.projectChanges);
        this.projectAgeDaysCount = calculateProjectDevelopmentDays(project.getGitCrawler().createdAt(), Date.from(Instant.now()));
        this.projectDevelopmentDaysCount = calculateProjectDevelopmentDays(project.getGitCrawler().createdAt(), project.getGitCrawler().lastModified());
        this.projectFilesCount = projectFiles.size();
        this.projectCodeCount = totalXLoc.getCodeLines();
        this.projectCommentCount = totalXLoc.getCommentLines();
        this.projectAuthorsCount = calculateTotalUniqueAuthors(this.projectAuthors);

        this.codeDistribution = new CodeDistribution(projectPath);
        this.faultDistribution = new FaultDistribution(project, projectPath);
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
        return projectChanges;
    }

    public Map<Path, Set<Author>> getProjectAuthors() {
        return projectAuthors;
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
        return projectAuthorsCount;
    }

    public Integer getProjectChangesCount() {
        return projectChangesCount;
    }

    public Integer getProjectAgeDaysCount() {
        return projectAgeDaysCount;
    }

    public Integer getProjectDevelopmentDaysCount() {
        return projectDevelopmentDaysCount;
    }

    public Integer getProjectFilesCount() {
        return projectFilesCount;
    }

    public Integer getProjectCodeCount() {
        return projectCodeCount;
    }

    public Integer getProjectCommentCount() {
        return projectCommentCount;
    }

    public CodeDistribution getCodeDistribution() {
        return codeDistribution;
    }

    public FaultDistribution getFaultDistribution() {
        return faultDistribution;
    }

    private Integer calculateProjectDevelopmentDays(Date start, Date end) {
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
