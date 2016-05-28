package org.uva.rdewildt.mt.report.overview;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.uva.rdewildt.mt.gcrawler.git.crawler.Crawler;
import org.uva.rdewildt.mt.gcrawler.git.crawler.FLocalCrawler;
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;
import org.uva.rdewildt.mt.gcrawler.git.model.Project;
import org.uva.rdewildt.mt.report.distribution.Distribution;
import org.uva.rdewildt.mt.utils.model.Percentage;
import org.uva.rdewildt.mt.xloc.XLoc;
import org.uva.rdewildt.mt.xloc.XLocCalculator;
import org.uva.rdewildt.mt.utils.lang.Java;
import org.uva.rdewildt.mt.utils.lang.Language;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.uva.rdewildt.mt.utils.MapUtils.*;

/**
 * Created by roy on 5/26/16.
 */
public class OverviewCalculator {
    private final Overview overview;

    public OverviewCalculator(Project project, Boolean ignoreGenerated, Boolean ignoreTests) throws IOException {
        Crawler crawler = new FLocalCrawler(project.getGitRoot(), ignoreGenerated, ignoreTests, new Java());

        Map<Path, XLoc> xlocs = new XLocCalculator(
                project.getGitRoot(), true,
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

            setFgini(round(faultdist.giniCoefficient(),2));
            setCgini(round(codedist.giniCoefficient(),2));
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

    private Integer getCodeIn20Percent(Map<String, Integer> faults, Map<Path, XLoc> xlocs){
        final double[] cloc = {0};
        Map<String, Integer> faulty = mapTakeByOrderedValue(faults, new Percentage(20.0));
        faulty.forEach((path, count) -> cloc[0] += xlocs.get(Paths.get(path)).getCodeLines());
        return (int) (cloc[0] / calculateTotalXLoc(xlocs).getCodeLines()* 100);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
