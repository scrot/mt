package report;

import collector.ClassBaseVisitor;
import com.messners.gitlab.api.GitLabApiException;
import gitcrawler.crawler.Crawler;
import gitcrawler.crawler.local.LocalCrawler;
import gitcrawler.model.Fault;
import gitcrawler.model.Project;
import lims.MetricCalculator;
import lims.MetricCounter;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static utils.Utils.addValueToMapList;

public class FeatureBuilder {
    private final List<Report> featureReports;

    public FeatureBuilder(Project project) throws IOException, ClassNotFoundException {
        this.featureReports = new ArrayList<>();
        addFeatureReport(project);
    }

    public FeatureBuilder(List<Project> projects) throws IOException, ClassNotFoundException {
        this.featureReports = new ArrayList<>();
        for(Project project : projects) {
            addFeatureReport(project);
        }
    }

    public void writeOverviewReportToFile(String seperator) throws IOException, GitLabApiException, GitAPIException {
        for(Report featureReport : featureReports){
            FileWriter writer = new FileWriter(featureReport.getName() + "_featureset.csv");
            writer.write(String.join(seperator, featureReport.getHeader()) + '\n');
            for(List<String> row : featureReport.getBody()){
                writer.write(String.join(seperator, row) + '\n');
            }
            writer.close();
        }
    }

    private void addFeatureReport(Project project) throws IOException, ClassNotFoundException, GitAPIException, GitLabApiException {
        Report featureReport = new Report(project.getProject(), new LinkedHashMap<>());

        Map<String, MetricCounter> metrics = new MetricCalculator(project.getBinaryPath(), true).getMetrics();
        Crawler crawler = new LocalCrawler(project);
        Map<Path, List<Fault>> faults = crawler.getFaults();

        for(Map.Entry<String,MetricCounter> metric : metrics.entrySet()){
            updateFeatureReport(featureReport, metric.getKey(), metric.getValue());
        }
        featureReports.add(featureReport);
    }

    private Report updateFeatureReport(Report report, String className, MetricCounter cm) throws IOException {
        Map<String, List<String>> rmap = report.getReport();
        addValueToMapList(rmap, "Class", className);
        addValueToMapList(rmap, "WMC", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "DIT", Integer.toString(cm.getDit()));
        addValueToMapList(rmap, "NOC", Integer.toString(cm.getNoc()));
        addValueToMapList(rmap, "CBO", Integer.toString(cm.getCbo()));
        addValueToMapList(rmap, "RFC", Integer.toString(cm.getRfc()));
        addValueToMapList(rmap, "LCOM", Integer.toString(cm.getLcom()));
        addValueToMapList(rmap, "DAC", Integer.toString(cm.getDac()));
        addValueToMapList(rmap, "MPC", Integer.toString(cm.getMpc()));
        addValueToMapList(rmap, "NOM", Integer.toString(cm.getNom()));
        addValueToMapList(rmap, "SIZE1", Integer.toString(cm.getSize1()));
        addValueToMapList(rmap, "SIZE2", Integer.toString(cm.getSize2()));
        //addValueToMapList(rmap, "IsNew", Integer.toString(-1));
        //addValueToMapList(rmap, "IsChg", Integer.toString(-1));
        //addValueToMapList(rmap, "AGE", Integer.toString(-1));
        //addValueToMapList(rmap, "U", Integer.toString(-1));
        //addValueToMapList(rmap, "S", Integer.toString(-1));
        return report;
    }
}