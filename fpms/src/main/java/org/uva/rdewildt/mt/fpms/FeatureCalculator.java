package org.uva.rdewildt.mt.fpms;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.uva.rdewildt.mt.fpms.git.crawler.Crawler;
import org.uva.rdewildt.mt.fpms.git.crawler.LocalCrawler;
import org.uva.rdewildt.mt.fpms.git.model.Commit;
import org.uva.rdewildt.mt.lims.MetricCalculator;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by roy on 5/22/16.
 */
public class FeatureCalculator extends MetricCalculator {
    private final Crawler gcrawler;
    private Map<String, Feature> features;

    public FeatureCalculator(Path binaryRoot, Path gitRoot) throws Exception {
        super(binaryRoot);
        this.gcrawler = new LocalCrawler(gitRoot);
        calculateFeatures();
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    private void calculateFeatures() {
        Map<String, Integer> classesFaults = mapListLenghts(gcrawler.getFaults());
        Map<String, Integer> classesChanges = mapListLenghts(gcrawler.getCommits());
        Map<String, Integer> classesAuthors = mapListLenghts(gcrawler.getAuthors());
        Map<String, Integer> classesAge = getClassAges(gcrawler.getCommits());


        this.features = new HashMap<>();
        for(Map.Entry<String, Integer> entry : classesChanges.entrySet()){
            if(this.getMetrics().containsKey(entry.getKey())){
                FeatureCounter feature = new FeatureCounter(entry.getKey());
                feature.setMetric(this.getMetrics().get(entry.getKey()));
                feature.incrementFaults(classesFaults.get(entry.getKey()));
                feature.incrementChanges(classesChanges.get(entry.getKey()));
                feature.incrementAuthors(classesAuthors.get(entry.getKey()));
                feature.incrementAge(classesAge.get(entry.getKey()));
                features.put(entry.getKey(), feature.getFeature());
            }
        }
    }


    private <T,U> Map<T, Integer> mapListLenghts(Map<T, ? extends Collection<U>> map){
        Map<T, Integer> counts = new HashMap<>();
        for(Map.Entry<T, ? extends Collection<U>> col : map.entrySet()){
            counts.put(col.getKey(), col.getValue().size());
        }
        return counts;
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
}
