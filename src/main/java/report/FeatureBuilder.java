package report;

import com.messners.gitlab.api.GitLabApiException;
import gitcrawler.model.Project;
import metrics.Metric;
import metrics.MetricCalculator;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.eclipse.jgit.api.errors.GitAPIException;
import utils.ClassCollector;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static utils.MapTransformation.addValueToMapList;

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

    private void addFeatureReport(Project project) throws IOException, ClassNotFoundException {
        Report featureReport = new Report(project.getProject(), new LinkedHashMap<>());

        Map<String, Metric> metrics = new MetricCalculator(project.getBinaryPath()).getMetrics();
        for(Map.Entry<String,Metric> metric : metrics.entrySet()){
            updateFeatureReport(featureReport, metric.getKey(), metric.getValue());
        }
        featureReports.add(featureReport);
    }

    private Report updateFeatureReport(Report report, String className, Metric cm) throws IOException {
        Map<String, List<String>> rmap = report.getReport();
        addValueToMapList(rmap, "Class", className);
        addValueToMapList(rmap, "WMC", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "DIT", Integer.toString(cm.getDit()));
        addValueToMapList(rmap, "NOC", Integer.toString(cm.getNoc()));
        addValueToMapList(rmap, "CBO", Integer.toString(cm.getCbo()));
        addValueToMapList(rmap, "RFC", Integer.toString(cm.getRfc()));
        addValueToMapList(rmap, "LCOM", Integer.toString(cm.getLcom()));
        /*
        addValueToMapList(rmap, "CTI", Integer.toString(-1));
        addValueToMapList(rmap, "CTM", Integer.toString(-1));
        addValueToMapList(rmap, "CTA", Integer.toString(-1));
        addValueToMapList(rmap, "NOM", Integer.toString(-1));
        addValueToMapList(rmap, "SIZE1", Integer.toString(-1));
        addValueToMapList(rmap, "SIZE2", Integer.toString(-1));
        addValueToMapList(rmap, "IsNew", Integer.toString(-1));
        addValueToMapList(rmap, "IsChg", Integer.toString(-1));
        addValueToMapList(rmap, "AGE", Integer.toString(-1));
        addValueToMapList(rmap, "U", Integer.toString(-1));
        addValueToMapList(rmap, "S", Integer.toString(-1));
        */
        return report;
    }
}