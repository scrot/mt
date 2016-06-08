package org.uva.rdewildt.mt.fpms;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.uva.rdewildt.mt.gcrawler.git.crawler.CLocalCrawler;
import org.uva.rdewildt.mt.gcrawler.git.crawler.Crawler;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;
import org.uva.rdewildt.mt.utils.lang.Java;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by roy on 6/8/16.
 */
public class FeatureCalculator2 extends MetricCalculator2 {
    private final Crawler gcrawler;
    private Map<String, Feature> features;

    public FeatureCalculator2(Path binaryRoot, Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests) throws Exception {
        super(binaryRoot);
        this.gcrawler = new CLocalCrawler(gitRoot, ignoreGenerated, ignoreTests, new Java());
        calculateFeatures();
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    private void calculateFeatures() throws NoSuchFieldException {
        Map<String, Integer> fileFaults = outerClassSum(gcrawler.getFaults());
        Map<String, Integer> fileChanges = outerClassSum(gcrawler.getChanges());
        //Map<String, Integer> fileAuthors = outerClassSum(gcrawler.getAuthors());
        //Map<String, Integer> fileAge = getClassAges(gcrawler.getChanges());


        this.features = new HashMap<>();
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

    public <U> Map<String, Integer> outerClassSum(Map<String, ? extends Collection<U>> map){
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
}
