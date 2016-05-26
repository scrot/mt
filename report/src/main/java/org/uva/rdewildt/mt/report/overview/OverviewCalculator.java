package org.uva.rdewildt.mt.report.overview;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.uva.rdewildt.mt.fpms.git.crawler.Crawler;
import org.uva.rdewildt.mt.fpms.git.crawler.FLocalCrawler;
import org.uva.rdewildt.mt.fpms.git.model.Commit;
import org.uva.rdewildt.mt.fpms.git.model.Project;
import org.uva.rdewildt.mt.report.distribution.Distribution;
import org.uva.rdewildt.mt.report.distribution.Percentage;
import org.uva.rdewildt.mt.xloc.XLoc;
import org.uva.rdewildt.mt.xloc.XLocCalculator;
import org.uva.rdewildt.mt.xloc.lang.Java;
import org.uva.rdewildt.mt.xloc.lang.Language;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by roy on 5/26/16.
 */
public class OverviewCalculator {
    private final Overview overview;

    public OverviewCalculator(Project project, Boolean ignoreGenerated, Boolean ignoreTests) throws IOException {
        Crawler crawler = new FLocalCrawler(project.getGitRoot(), ignoreGenerated, ignoreTests, new Java());

        Map<Path, XLoc> xlocs = new XLocCalculator(
                project.getGitRoot(),
                ignoreGenerated, ignoreGenerated,
                new ArrayList<Language>(){{add(new Java());}}).getResult();
        XLoc totalXloc = calculateTotalXLoc(xlocs);

        SortedSet<Commit> sorted = getSortedSet(crawler.getChanges());

        Distribution faultdist = new Distribution(mapListLenghts(crawler.getFaults()));
        Distribution codedist = new Distribution(getCodeCounts(xlocs));

        this.overview = new Overview(project.getProject()){{
            setFiles(crawler.getChanges().size());
            setCloc(totalXloc.getCodeLines());
            setSloc(totalXloc.getCommentLines());

            setChanges(mapTotalListLenghts(crawler.getChanges()));
            setFaults(mapTotalListLenghts(crawler.getFaults()));
            setAuthors(calculateUniqueElements(crawler.getAuthors()));

            setAge(calculateDateDayDiff(sorted.first().getDate(), DateTime.now().toDate()));
            setDev(calculateDateDayDiff(sorted.first().getDate(), sorted.last().getDate()));

            setFdist(get20Percent(faultdist));
            setCinF(getCodeIn20Percent(mapListLenghts(crawler.getFaults()), xlocs));

            setFgini(faultdist.giniCoefficient());
            setCgini(codedist.giniCoefficient());
        }};

    }

    public Overview getOverview() {
        return overview;
    }


    private Integer calculateDateDayDiff(Date start, Date end) {
        DateTime startt = new DateTime(start);
        DateTime endt = new DateTime(end);
        Days d = Days.daysBetween(startt, endt);
        return d.getDays();
    }

    private <T> XLoc calculateTotalXLoc(Map<T, XLoc> classesXLoc) {
        XLoc totalXLoc = new XLoc(0,0,0,0);
        for(Map.Entry<T, XLoc> entry : classesXLoc.entrySet()){
            totalXLoc = totalXLoc.add(entry.getValue());
        }
        return totalXLoc;
    }

    private <T> Map<T, Integer> getCodeCounts(Map<T, XLoc> values){
        Map<T, Integer> counts = new HashMap<>();

        for(Map.Entry<T, XLoc> entry : values.entrySet()){
            counts.put(entry.getKey(), entry.getValue().getCodeLines());
        }

        return counts;
    }

    private Integer get20Percent(Distribution d){
        return d.cumulativeTailOfPartitionPercentage(new Percentage(20.0)).getPercentage().intValue();
    }

    private Double getCodeIn20Percent(Map<String, Integer> faults, Map<Path, XLoc> xlocs){
        final double[] cloc = {0};
        Map<String, Integer> faulty = mapTakeByOrderedValue(faults, new Percentage(20.0));
        faulty.forEach((path, count) -> cloc[0] += xlocs.get(Paths.get(path)).getCodeLines());
        return cloc[0] / calculateTotalXLoc(xlocs).getCodeLines() * 100;
    }

    private <T> Map<T, Integer> mapTakeByOrderedValue(Map<T, Integer> map, Percentage percentage){
        int limit =  (int) (percentage.getPercentage0to1() * map.size());
        SortedMap<Integer, T> sorted = new TreeMap<>(mapSwapKeyValue(map));
        return mapSwapKeyValue(mapTake(sorted, limit));

    }

    private<T,U> SortedMap<T,U> mapTake(SortedMap<T,U> map, int limit){
        SortedMap<T,U> head = new TreeMap<>();
        int i = 0;
        for(Map.Entry<T,U> entry : map.entrySet()){
            if(i < limit){
                head.put(entry.getKey(), entry.getValue());
            }
            else {
                break;
            }
        }
        return head;
    }

    private <T,U> Map<U,T> mapSwapKeyValue(Map<T,U> map){
        Map<U,T> rev = new HashMap<>();
        for(Map.Entry<T,U> entry : map.entrySet()){
            rev.put(entry.getValue(), entry.getKey());
        }
        return rev;
    }

    private <T,U> Integer mapTotalListLenghts(Map<T, ? extends Collection<U>> map){
        Integer size = 0;

        for(Collection<U> entry : map.values()){
            size += entry.size();
        }

        return size;
    }

    private <T,U> Map<T, Integer> mapListLenghts(Map<T, ? extends Collection<U>> map){
        Map<T, Integer> lengths = new HashMap<>();

        for(Map.Entry<T, ? extends Collection> entry : map.entrySet()){
            lengths.put(entry.getKey(), entry.getValue().size());
        }

        return lengths;
    }

    private <T,U> Integer calculateUniqueElements(Map<T, ? extends Collection<U>> map){
        Set<U> uniqueAuthors = new HashSet<>();
        for(Map.Entry<T, ? extends Collection> entry : map.entrySet()){
            uniqueAuthors.addAll(entry.getValue());
        }
        return uniqueAuthors.size();
    }


    private <T,U> SortedSet<U> getSortedSet(Map<T, ? extends Collection<U>> commits){
        TreeSet<U> flatCommits = new TreeSet<>();
        commits.forEach((s, commitlist) -> flatCommits.addAll(commitlist));
        return flatCommits;
    }

}
