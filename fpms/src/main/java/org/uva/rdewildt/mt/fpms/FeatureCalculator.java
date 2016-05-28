package org.uva.rdewildt.mt.fpms;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.uva.rdewildt.mt.gcrawler.git.crawler.CLocalCrawler;
import org.uva.rdewildt.mt.gcrawler.git.crawler.Crawler;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;
import org.uva.rdewildt.mt.lims.MetricCalculator;
import org.uva.rdewildt.mt.utils.lang.Java;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.uva.rdewildt.mt.utils.MapUtils.mapListLenghts;

/**
 * Created by roy on 5/22/16.
 */
public class FeatureCalculator extends MetricCalculator {
    private final Crawler gcrawler;
    private Map<String, Feature> features;

    public FeatureCalculator(Path binaryRoot, Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests) throws Exception {
        super(binaryRoot);
        this.gcrawler = new CLocalCrawler(gitRoot, ignoreGenerated, ignoreTests, new Java());
        calculateFeatures();
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    private void calculateFeatures() {
        Map<String, Integer> classesFaults = mapListLenghts(gcrawler.getFaults());
        Map<String, Integer> classesChanges = mapListLenghts(gcrawler.getChanges());
        Map<String, Integer> classesAuthors = mapListLenghts(gcrawler.getAuthors());
        Map<String, Integer> classesAge = getClassAges(gcrawler.getChanges());


        this.features = new HashMap<>();
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
