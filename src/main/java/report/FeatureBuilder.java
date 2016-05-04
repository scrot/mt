package report;

import com.messners.gitlab.api.GitLabApiException;
import git.model.Project;
import lang.Java;
import metrics.ClassMetrics;
import metrics.ClassMetricsContainer;
import metrics.ClassVisitor;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.eclipse.jgit.api.errors.GitAPIException;
import utils.PathsCollector;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static utils.MapTransformation.addValueToMapList;

public class FeatureBuilder {
    private final List<Report> featureReports;

    public FeatureBuilder(Project project) throws IOException {
        this.featureReports = new ArrayList<>();
        addFeatureReport(project);
    }

    public FeatureBuilder(List<Project> projects) throws IOException {
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

    private void addFeatureReport(Project project) throws IOException {
        List<Path> javaClassPaths = new PathsCollector(project.getLocalPath()).collectClassPaths(new Java());
        Report featureReport = new Report(project.getProject(), new LinkedHashMap<>());
        for(Path javaClassPath : javaClassPaths) {
            updateFeatureReport(featureReport, javaClassPath);
        }
        featureReports.add(featureReport);
    }

    private Report updateFeatureReport(Report report, Path javaClassPath) throws IOException {
        ClassMetrics cm = calculateMetrics(javaClassPath);
        Map<String, List<String>> rmap = report.getReport();
        addValueToMapList(rmap, "Class", javaClassPath.toString());
        addValueToMapList(rmap, "WMC", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "DIT", Integer.toString(cm.getDit()));
        addValueToMapList(rmap, "NOC", Integer.toString(cm.getNoc()));
        addValueToMapList(rmap, "CBO", Integer.toString(cm.getCbo()));
        addValueToMapList(rmap, "RFC", Integer.toString(cm.getRfc()));
        addValueToMapList(rmap, "LCOM", Integer.toString(cm.getLcom()));
        addValueToMapList(rmap, "CTI", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "CTM", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "CTA", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "NOM", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "SIZE1", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "SIZE2", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "IsNew", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "IsChg", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "AGE", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "U", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "S", Integer.toString(cm.getWmc()));
        addValueToMapList(rmap, "Ca", Integer.toString(cm.getCa()));
        addValueToMapList(rmap, "NPM", Integer.toString(cm.getNpm()));
        return report;
    }

    private ClassMetrics calculateMetrics(Path javaClassPath) throws IOException {
        JavaClass jc = new ClassParser(javaClassPath.toString()).parse();
        ClassVisitor visitor = new ClassVisitor(jc, new ClassMetricsContainer());
        visitor.start();
        visitor.end();
        return visitor.getMetrics();
    }
}
