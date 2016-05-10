package report;

import com.messners.gitlab.api.GitLabApiException;
import gitcrawler.model.Project;
import javassist.compiler.Javac;
import metrics.Metric;
import metrics.ClassVisitor;
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
        Report featureReport = new Report(project.getProject(), new LinkedHashMap<>());

        List<JavaClass> classes = collectClasses(project.getJarPath());
        calculateMetrics(classes);
        featureReports.add(featureReport);
    }

    private Report updateFeatureReport(Report report, Path sourcePath, Metric cm) throws IOException {
        Map<String, List<String>> rmap = report.getReport();
        addValueToMapList(rmap, "Class", sourcePath.toString());
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
        addValueToMapList(rmap, "Ca", Integer.toString(cm.getCa()));
        addValueToMapList(rmap, "NPM", Integer.toString(cm.getNpm()));
        */
        return report;
    }

    private List<JavaClass> collectClasses(Path classesRoot) throws IOException {
        List<JavaClass> classes = new ArrayList<>();
        List<Path> javaClassPaths = new ClassCollector(classesRoot).collectClassPaths();

        for(Path javaClassPath : javaClassPaths){
            if(classesRoot.toFile().isDirectory()){
                classes.add(new ClassParser(javaClassPath.toString()).parse());
            }
            else {
                classes.add(new ClassParser(classesRoot.toString(), javaClassPath.toString().substring(1)).parse());
            }
        }
        return classes;
    }

    private Map<Path, List<JavaClass>> buildClassSourceMap(Project project) throws IOException {
        Map<Path, List<JavaClass>> map = new HashMap<>();
        List<Path> javaClassPaths = new ClassCollector(project.getJarPath()).collectClassPaths();

        for(Path javaClassPath : javaClassPaths){
            JavaClass jc;
            if(project.getJarPath().toFile().isDirectory()){
                jc = new ClassParser(javaClassPath.toString()).parse();
            }
            else {
                jc = new ClassParser(project.getJarPath().toString(), javaClassPath.toString().substring(1)).parse();
            }
            if(!jc.getPackageName().equals("") && !jc.getSourceFileName().equals("<Unknown>")){
                Path sourcePath = Paths.get(jc.getPackageName().replace('.', '/') + "/" + jc.getSourceFileName());
                addValueToMapList(map, sourcePath, jc);
            }
        }

        return map;
    }

    private Metric calculateMetrics(List<JavaClass> classes) throws IOException {
        ClassVisitor visitor = new ClassVisitor(classes);
        return null;
    }
}