package report;

import com.messners.gitlab.api.GitLabApiException;
import distr.Distribution;
import distr.Percentage;
import gitcrawler.crawler.Crawler;
import gitcrawler.crawler.local.LocalCrawler;
import gitcrawler.model.*;
import lang.Java;
import lang.Language;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.joda.time.DateTime;
import org.joda.time.Days;
import utils.SourceCollector;
import xloc.XLoc;
import xloc.XLocCalculator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static utils.MapTransformation.*;

public class OverviewBuilder {
    private final Report overviewReport;

    public OverviewBuilder(String reportName, Project project) throws IOException, GitLabApiException, GitAPIException {
        this.overviewReport = new Report(reportName, new LinkedHashMap<>());
        updateOverviewReport(this.overviewReport, project);
    }

    public OverviewBuilder(String reportName, List<Project> projects) throws IOException, GitLabApiException, GitAPIException {
        this.overviewReport = new Report(reportName, new LinkedHashMap<>());

        int i = 0;
        for(Project project : projects){
            System.out.println("Building report " + ++i + " /" + projects.size());
            updateOverviewReport(this.overviewReport, project);
        }
    }

    public void writeOverviewReportToFile(String seperator) throws IOException, GitLabApiException, GitAPIException {
        String writername = overviewReport.getName() + "_overview.csv";
        FileWriter writer = new FileWriter(writername);
        writer.write(String.join(seperator, this.overviewReport.getHeader()) + '\n');

        for(List<String> row : this.overviewReport.getBody()){
            writer.write(String.join(seperator, row) + '\n');
        }
        writer.close();
    }

    private Report updateOverviewReport(Report report, Project project) throws GitAPIException, IOException, GitLabApiException {
        Map<String, List<String>> rmap = report.getReport();

        List<Language> languageScope = new ArrayList<Language>(){{add(new Java());}};
        Crawler crawler = new LocalCrawler(project);

        List<Path> projectFiles = new SourceCollector(project.getLocalPath()).collectFilePaths();
        Map<Path, XLoc> classesXLoc = new XLocCalculator(project.getLocalPath(), languageScope, true).getResult();
        XLoc totalXLoc = calculateTotalXLoc(classesXLoc);

        Map<Path, List<Fault>> codeFaults = filterContains(crawler.getFaults(), classesXLoc);
        Map<Path, Set<Commit>> codeChanges = filterContains(crawler.getChanges(), classesXLoc);
        Map<Path, Set<Author>> codeAuthors = filterContains(crawler.getAuthors(), classesXLoc);

        List<Commit> commitSorted = sortCommits(crawler.getCommits());
        Commit firstCommit = commitSorted.get(0);
        Commit lastCommit = commitSorted.get(commitSorted.size() - 1);

        Distribution faultDistribution = new Distribution(valueCounts(codeFaults));
        Distribution codeDistribution = new Distribution(getCodeCounts(classesXLoc));

        DecimalFormat formatter = new DecimalFormat("#.##");
        addValueToMapList(rmap, "Project", project.getProject());
        addValueToMapList(rmap, "TotFiles", Integer.toString(projectFiles.size()));
        addValueToMapList(rmap, "CodeFiles", Integer.toString(classesXLoc.size()));
        addValueToMapList(rmap, "CodeLines", Integer.toString(totalXLoc.getCodeLines()));
        addValueToMapList(rmap, "CommLines", Integer.toString(totalXLoc.getCommentLines()));
        addValueToMapList(rmap, "DevDays", Integer.toString(calculateDateDayDiff(firstCommit.getDate(), lastCommit.getDate())));
        addValueToMapList(rmap, "AgeDays", Integer.toString(calculateDateDayDiff(firstCommit.getDate(), new Date())));
        addValueToMapList(rmap, "TotCommits", Integer.toString(crawler.getCommits().size()));
        addValueToMapList(rmap, "CodeCommits", Integer.toString(extractCommitsFromFaults(codeFaults).size()));
        addValueToMapList(rmap, "TotIssues", Integer.toString(crawler.getIssues().size()));
        addValueToMapList(rmap, "CodeIssues", Integer.toString(extractIssuesFromFaults(codeFaults).size()));
        addValueToMapList(rmap, "TotFaults", Integer.toString(crawler.getFaults().size()));
        addValueToMapList(rmap, "CodeFaults", Integer.toString(codeFaults.size()));
        addValueToMapList(rmap, "TotChanges", Integer.toString(crawler.getChanges().size()));
        addValueToMapList(rmap, "CodeChanges", Integer.toString(codeChanges.size()));
        addValueToMapList(rmap, "TotAuthors", Integer.toString(calculateTotalUniqueAuthors(crawler.getAuthors())));
        addValueToMapList(rmap, "CodeAuthors", Integer.toString(calculateTotalUniqueAuthors(codeAuthors)));
        addValueToMapList(rmap, "FaultDist", "20-" + this.get20Percent(faultDistribution));
        addValueToMapList(rmap, "FaultCode", "20-" +  getCodeIn20Percent(codeFaults, classesXLoc));
        addValueToMapList(rmap, "FaultGini", formatter.format(faultDistribution.giniCoefficient()));
        addValueToMapList(rmap, "CodeGini", formatter.format(codeDistribution.giniCoefficient()));
        return report;
    }

    private Integer getCodeIn20Percent(Map<Path, List<Fault>> codeFaults, Map<Path, XLoc> clocs){
        List<FPath> faulty = getMostFaultyFiles(codeFaults, new Percentage(20.0));
        Integer faulyCloc = codeInFiles(faulty, clocs);
        return new Percentage(faulyCloc / calculateTotalXLoc(clocs).getCodeLines().doubleValue() * 100).getPercentage().intValue();
    }

    private List<FPath> getMostFaultyFiles(Map<Path, List<Fault>> codeFaults, Percentage percentage){
        List<FPath> faulty = new ArrayList<>();
        for(Map.Entry<Path, List<Fault>> entry : codeFaults.entrySet()){
            faulty.add(new FPath(entry.getKey(), entry.getValue().size()));
        }
        faulty.sort(Comparator.comparing(FPath::getFaultCount).reversed());
        return faulty.subList(0, (int)(codeFaults.size() * percentage.getPercentage0to1()));
    }

    private Integer codeInFiles(List<FPath> files, Map<Path,XLoc> locs){
        int cloc = 0;
        for(FPath file : files){
            cloc += locs.get(file.getPath()).getCodeLines();
        }
        return cloc;
    }

    private Integer get20Percent(Distribution d){
        return d.cumulativeTailOfPartitionPercentage(new Percentage(20.0)).getPercentage().intValue();
    }

    private <T> Map<T, Integer> getCodeCounts(Map<T, XLoc> values){
        Map<T, Integer> counts = new HashMap<>();

        for(Map.Entry<T, XLoc> entry : values.entrySet()){
            counts.put(entry.getKey(), entry.getValue().getCodeLines());
        }

        return counts;
    }

    private List<Commit> sortCommits(Map<Object, Commit> commits) {
        return commits.values().stream()
                .sorted(Comparator.comparing(Commit::getDate))
                .collect(Collectors.toList());
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
                if(fault.getIssue() != null){
                    filteredIssues.put(fault.getIssue().getId(), fault.getIssue());
                }
            }
        }
        return filteredIssues;
    }

    private Map<Object, Commit> extractCommitsFromFaults(Map<Path, List<Fault>> faults) {
        Map<Object, Commit> filteredCommits = new HashMap<>();
        for(List<Fault> entry : faults.values()){
            for(Fault fault : entry){
                if(fault.getCommit() != null){
                    filteredCommits.put(fault.getCommit().getId(), fault.getCommit());
                }
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

    private XLoc calculateTotalXLoc(Map<Path, XLoc> classesXLoc) {
        XLoc totalXLoc = new XLoc(0,0,0,0);
        for(Map.Entry<Path, XLoc> entry : classesXLoc.entrySet()){
            totalXLoc = totalXLoc.add(entry.getValue());
        }
        return totalXLoc;
    }


}
