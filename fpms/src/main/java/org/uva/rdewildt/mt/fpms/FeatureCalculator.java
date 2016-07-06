package org.uva.rdewildt.mt.fpms;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.uva.rdewildt.mt.gcrawler.git.GitUtils;
import org.uva.rdewildt.mt.gcrawler.git.crawler.ClassCrawler;
import org.uva.rdewildt.mt.gcrawler.git.crawler.Crawler;
import org.uva.rdewildt.mt.gcrawler.git.crawler.FileCrawler;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;
import org.uva.rdewildt.mt.bcms.MetricCalculator;
import org.uva.rdewildt.mt.gcrawler.git.model.Fault;
import org.uva.rdewildt.mt.utils.MapUtils;
import org.uva.rdewildt.mt.utils.lang.Java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.uva.rdewildt.mt.utils.MapUtils.mapListLenghts;

/**
 * Created by roy on 5/22/16.
 */
public class FeatureCalculator extends MetricCalculator {
    private final Map<String, Feature> features;

    public FeatureCalculator(Path binaryRoot, Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests, Boolean onlyOuterClasses, Boolean stateAware) throws Exception {
        super(binaryRoot, onlyOuterClasses);
        if(onlyOuterClasses){
            if(stateAware){
                if(!binaryRoot.toFile().isDirectory()){
                    throw new Exception("Binary root path is not a directory");
                }
                this.features = calculateOuterClassStateFeaturesGreedy(gitRoot, ignoreGenerated, ignoreTests);
            }
            else {
                this.features = calculateOuterClassFeaturesGreedy(gitRoot, ignoreGenerated, ignoreTests);
            }
        }
        else{
            this.features = calculateAllFeatures(gitRoot, ignoreGenerated, ignoreTests);
        }
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    private Map<String, Feature> calculateAllFeatures(Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests) throws Exception {
        Crawler gcrawler = new ClassCrawler(gitRoot, ignoreGenerated, ignoreTests, new Java());
        Map<String, Integer> classesFaults = mapListLenghts(gcrawler.getFaults());
        Map<String, Integer> classesChanges = mapListLenghts(gcrawler.getChanges());
        Map<String, Integer> classesAuthors = mapListLenghts(gcrawler.getAuthors());
        Map<String, Integer> classesAge = getClassAges(gcrawler.getChanges());


        Map<String, Feature> features = new HashMap<>();
        for(Map.Entry<String, Integer> entry : classesChanges.entrySet()){
            if(this.getMetrics().containsKey(entry.getKey())){
                Feature feature = new Feature(
                        this.getMetrics().get(entry.getKey()),
                        classesFaults.get(entry.getKey()),
                        classesChanges.get(entry.getKey()),
                        classesAuthors.get(entry.getKey()),
                        classesAge.get(entry.getKey()));
                features.put(entry.getKey(), feature);
            }
        }

        return features;
    }

    private Map<String, Feature> calculateOuterClassFeatures(Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests) throws Exception {
        Crawler gcrawler = new ClassCrawler(gitRoot, ignoreGenerated, ignoreTests, new Java());
        Map<String, Integer> fileFaults = outerClassSum(gcrawler.getFaults());
        Map<String, Integer> fileChanges = outerClassSum(gcrawler.getChanges());


        Map<String, Feature> features = new HashMap<>();
        fileChanges.forEach((k,v) -> {
            if(this.getMetrics().containsKey(k)){
                try {
                    Feature feature = new Feature(
                            this.getMetrics().get(k),
                            fileFaults.get(k),
                            fileChanges.get(k),
                            0,
                            0);
                    features.put(k, feature);
                } catch (NoSuchFieldException e) {e.printStackTrace();}
            }
        });
        return features;
    }

    private Map<String, Feature> calculateOuterClassFeaturesGreedy(Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests) {
        Crawler gcrawler = null;
        try {
            gcrawler = new FileCrawler(gitRoot, ignoreGenerated, ignoreTests, false, new Java());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Integer> classesFaults = mapListLenghts(gcrawler.getFaults());
        Map<String, Integer> classesChanges = mapListLenghts(gcrawler.getChanges());
        Map<String, Integer> classesAuthors = mapListLenghts(gcrawler.getAuthors());
        Map<String, Integer> classesAge = getClassAges(gcrawler.getChanges());

        Map<String, Feature> features = new HashMap<>();
        classesChanges.forEach((k,v) -> {
            if(this.getMetrics().containsKey(k)){
                try {
                    Feature feature = new Feature(
                            this.getMetrics().get(k),
                            classesFaults.get(k),
                            classesChanges.get(k),
                            classesAuthors.get(k),
                            classesAge.get(k));
                    features.put(k, feature);
                } catch (NoSuchFieldException e) {e.printStackTrace();}
            }
        });

        return features;
    }

    private Map<String, Feature> calculateOuterClassStateFeaturesGreedy(Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests) throws IOException {
        Map<String, Feature> features = calculateOuterClassFeaturesGreedy(gitRoot, ignoreGenerated, ignoreTests);

        Crawler gcrawler = new FileCrawler(gitRoot, ignoreGenerated, ignoreTests, false, new Java());
        Map<String, Set<Fault>> classFaults = gcrawler.getFaults();
        Map<Fault, Set<String>> faultClasses = getFaultClasses(classFaults);
        Set<Fault> faults = getFaults(classFaults);

       classFaults.forEach((k,v) -> features.remove(k));

        faults.forEach(fault -> {
            String faultCommitId = fault.getCommit().getId().toString();
            System.out.println("Resetting system state to commit " + faultCommitId + "...");
            GitUtils.gitReset(gitRoot, faultCommitId);
            System.out.println("Recompiling classes...");
            BuildUtils.buildProject(gitRoot);
            System.out.println("Calculating features...");
            Map<String, Feature> stateFeatures = calculateOuterClassFeaturesGreedy(gitRoot, ignoreGenerated, ignoreTests);
            faultClasses.get(fault).forEach(clazz -> features.put(clazz + '$' + faultCommitId, stateFeatures.get(clazz)));
        });

        return features;
    }

    private Map<Fault, Set<String>> getFaultClasses(Map<String, Set<Fault>> classFaults) {
        Map<Fault, Set<String>> faultclasses = new HashMap<>();
        classFaults.forEach((k,v) -> v.forEach(fault -> MapUtils.addValueToMapSet(faultclasses, fault, k)));
        return faultclasses;
    }

    private Set<Fault> getFaults(Map<String, Set<Fault>> classesFaults) {
        Set<Fault> faults = new HashSet<>();
        classesFaults.forEach((k,v) -> v.forEach(faults::add));
        return faults;
    }


    private List<Commit> sortCommits(Set<Commit> commits) {
        return commits.stream()
                .sorted(Comparator.comparing(Commit::getDate))
                .collect(Collectors.toList());
    }

    private <T> Map<T, Integer> getClassAges(Map<T, Set<Commit>> map) {
        Map<T, Integer> counts = new HashMap<>();
        for(Map.Entry<T, Set<Commit>> entry : map.entrySet()){
            List<Commit> sorted = sortCommits(entry.getValue());
            Date first = sorted.get(0).getDate();
            Date last = sorted.get(sorted.size() - 1).getDate();
            counts.put(entry.getKey(), calculateDateDayDiff(first, last));
        }
        return counts;
    }

    private Integer calculateDateDayDiff(Date start, Date end) {
        DateTime startt = new DateTime(start);
        DateTime endt = new DateTime(end);
        Days d = Days.daysBetween(startt, endt);
        return d.getDays();
    }

    private <U> Map<String, Integer> outerClassSum(Map<String, ? extends Collection<U>> map){
        Map<String, Integer> counts = new HashMap<>();
        for(Map.Entry<String, ? extends Collection<U>> col : map.entrySet()){
            String outer = getOuterClass(col.getKey());
            if(counts.containsKey(outer)){
                int size = counts.get(outer);
                size += col.getValue().size();
                counts.put(outer, size);
            }
            else{
                counts.put(outer, col.getValue().size());
            }
        }
        return counts;
    }

    private String getOuterClass(String classname){
        if(classname.contains("$")){
            return classname.substring(0,classname.indexOf("$"));
        }
        else {
            return classname;
        }
    }

    private void cleanWorkingDir(Path gitRoot){
        File root = new File(gitRoot.toUri());
        File[] files = root.listFiles(file -> !file.toString().endsWith(".git"));
        Arrays.stream(files).forEach(file -> {
            if(file.isDirectory()){
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
