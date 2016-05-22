package feature;

import collector.SourceVisitor;
import collector.model.ClassSource;
import gitcrawler.crawler.Crawler;
import gitcrawler.crawler.local.LocalCrawler;
import gitcrawler.model.Commit;
import lims.Metric;
import lims.MetricCalculator;
import org.joda.time.DateTime;
import org.joda.time.Days;
import xloc.SourceCollector;
import xloc.lang.Language;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by roy on 5/22/16.
 */
public class FeatureCalculator {
    private final Map<String, Feature> features;
    private final Map<String, Metric> classMetrics;
    private final Map<String, Integer> classesFaults;
    private final Map<String, Integer> classesChanges;
    private final Map<String, Integer> classesAuthors;
    private final Map<String, Integer> classesAge;

    private final Commit head;
    private final Path gitRoot;

    public FeatureCalculator(Path binaryRoot, Path gitRoot, Boolean ignoreInnerClasses, Boolean ignoreTests, Language ofLanguage) throws Exception {
        Crawler gcrawler = new LocalCrawler(gitRoot);
        MetricCalculator mcalc = new MetricCalculator(binaryRoot, ignoreInnerClasses);

        this.classMetrics = mcalc.getMetrics();
        this.classesFaults = new HashMap<>();
        this.classesChanges = new HashMap<>();
        this.classesAuthors = new HashMap<>();
        this.classesAge = new HashMap<>();

        this.head = sortCommits(new HashSet<>(gcrawler.getCommits().values())).get(gcrawler.getCommits().size() -1);
        this.gitRoot = gitRoot;

        List<Path> filePaths = new SourceCollector(gitRoot, ignoreInnerClasses, ignoreTests).collectFilePaths(ofLanguage);
        for(Path filePath : filePaths){
            this.classesFaults.putAll(fileToClassCountList(filePath, gcrawler.getFaults()));
            this.classesChanges.putAll(fileToClassCountSet(filePath, gcrawler.getChanges()));
            this.classesAuthors.putAll(fileToClassCountSet(filePath, gcrawler.getAuthors()));
            this.classesAge.putAll(fileToClassCount(filePath, changeAges(gcrawler.getChanges())));
        }


        this.features = new HashMap<>();
        for(Map.Entry<String, Metric> entry : classMetrics.entrySet()){
            FeatureCounter feature = new FeatureCounter();
            feature.setMetric(entry.getValue());
            feature.incrementFaults(this.classesFaults.get(entry.getKey()));
            feature.incrementFaults(this.classesChanges.get(entry.getKey()));
            feature.incrementFaults(this.classesAuthors.get(entry.getKey()));
            feature.incrementFaults(this.classesAge.get(entry.getKey()));
            features.put(entry.getKey(), feature.getFeature());
        }
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    private <T> Map<String, Integer> fileToClassCountSet(Path file, Map<Path, Set<T>> fileCounts) throws IOException {
        Map<String, Integer> faultCounts = new HashMap<>();

        List<ClassSource> classSources = new SourceVisitor(file).getClassSources();
        for(ClassSource classSource : classSources){
            Path relative = file.relativize(this.gitRoot);
            if(fileCounts.get(relative) != null) {
                faultCounts.put(classSource.getClassName(), fileCounts.get(relative).size());
            }
        }
        return faultCounts;
    }

    private <T> Map<String, Integer> fileToClassCountList(Path file, Map<Path, List<T>> fileCounts) throws IOException {
        Map<String, Integer> faultCounts = new HashMap<>();

        List<ClassSource> classSources = new SourceVisitor(file).getClassSources();
        for(ClassSource classSource : classSources){
            Path relative = file.relativize(this.gitRoot);
            if(fileCounts.get(relative) != null){
                faultCounts.put(classSource.getClassName(), fileCounts.get(relative).size());
            }
        }
        return faultCounts;
    }

    private Map<String, Integer> fileToClassCount(Path file, Map<Path, Integer> fileCounts) throws IOException {
        Map<String, Integer> faultCounts = new HashMap<>();

        List<ClassSource> classSources = new SourceVisitor(file).getClassSources();
        for(ClassSource classSource : classSources){
            Path relative = file.relativize(this.gitRoot);
            if(fileCounts.get(relative) != null) {
                faultCounts.put(classSource.getClassName(), fileCounts.get(relative));
            }
        }
        return faultCounts;
    }

    private Map<Path, Integer> changeAges(Map<Path, Set<Commit>> changes) {
        Map<Path, Integer> filesAge = new HashMap<>();
        for(Map.Entry<Path, Set<Commit>> change : changes.entrySet()){
            List<Commit> sorted = sortCommits(change.getValue());
            Integer age = calculateDateDayDiff(sorted.get(0).getDate(), this.head.getDate());
            filesAge.put(change.getKey(), age);
        }
        return filesAge;
    }

    private List<Commit> sortCommits(Set<Commit> commits) {
        return commits.stream()
                .sorted(Comparator.comparing(Commit::getDate))
                .collect(Collectors.toList());
    }


    private Integer calculateDateDayDiff(Date start, Date end) {
        DateTime startt = new DateTime(start);
        DateTime endt = new DateTime(end);
        Days d = Days.daysBetween(startt, endt);
        return d.getDays();
    }
}
