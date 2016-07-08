package org.uva.rdewildt.mt.ovms;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.uva.rdewildt.mt.gcrawler.git.Crawler;
import org.uva.rdewildt.mt.gcrawler.git.FileCrawler;
import org.uva.rdewildt.mt.utils.model.git.Commit;
import org.uva.rdewildt.mt.ovms.distribution.Distribution;
import org.uva.rdewildt.mt.utils.MapUtils;
import org.uva.rdewildt.mt.utils.model.lang.Java;
import org.uva.rdewildt.mt.utils.model.lang.Language;
import org.uva.rdewildt.mt.utils.model.Percentage;
import org.uva.rdewildt.mt.xloc.XLoc;
import org.uva.rdewildt.mt.xloc.XLocCalculator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.uva.rdewildt.mt.utils.MapUtils.mapListLenghts;
import static org.uva.rdewildt.mt.utils.MapUtils.mapValuesUniqueFlatmap;

/**
 * Created by roy on 5/26/16.
 */
public class OverviewCalculator {
    private final Overview overview;

    public OverviewCalculator(String projectName, Path projectRoot, Boolean ignoreGenerated, Boolean ignoreTests) throws IOException {
        Crawler crawler = new FileCrawler(projectRoot, ignoreGenerated, ignoreTests, true, new Java());
        Map<Path, XLoc> xlocs = new XLocCalculator(
                projectRoot, true,
                ignoreGenerated, ignoreGenerated,
                new ArrayList<Language>() {{
                    add(new Java());
                }}).getResult();
        XLoc totalXloc = calculateTotalXLoc(xlocs);

        SortedSet<Commit> sorted = MapUtils.getSortedSet(crawler.getChanges());

        Distribution faultdist = new Distribution(mapListLenghts(crawler.getFaults()));
        Distribution codedist = new Distribution(getCodeCounts(xlocs));

        this.overview = new Overview(projectName,
                crawler.getChanges().size(),
                totalXloc.getCodeLines(),
                totalXloc.getCommentLines(),
                mapValuesUniqueFlatmap(crawler.getFaults()).size(),
                mapValuesUniqueFlatmap(crawler.getChanges()).size(),
                mapValuesUniqueFlatmap(crawler.getAuthors()).size(),
                calculateDateDayDiff(sorted.first().getDate(), DateTime.now().toDate()),
                calculateDateDayDiff(sorted.first().getDate(), sorted.last().getDate()),
                get20Percent(faultdist),
                getCodeIn20Percent(mapListLenghts(crawler.getFaults()), xlocs),
                round(faultdist.giniCoefficient(), 2),
                round(codedist.giniCoefficient(), 2)
        );
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
        XLoc totalXLoc = new XLoc(0, 0, 0, 0);
        for (Map.Entry<T, XLoc> entry : classesXLoc.entrySet()) {
            totalXLoc = totalXLoc.add(entry.getValue());
        }
        return totalXLoc;
    }

    private <T> Map<T, Integer> getCodeCounts(Map<T, XLoc> values) {
        Map<T, Integer> counts = new HashMap<>();

        for (Map.Entry<T, XLoc> entry : values.entrySet()) {
            counts.put(entry.getKey(), entry.getValue().getCodeLines());
        }

        return counts;
    }

    private Integer get20Percent(Distribution d) {
        return d.cumulativeTailOfPartitionPercentage(new Percentage(20.0)).getPercentage().intValue();
    }

    private Integer getCodeIn20Percent(Map<String, Integer> faults, Map<Path, XLoc> xlocs) {
        final double[] cloc = {0};
        Map<String, Integer> faulty = MapUtils.mapTakeByOrderedValue(faults, new Percentage(20.0));
        faulty.forEach((path, count) -> cloc[0] += xlocs.get(Paths.get(path)).getCodeLines());
        return (int) (cloc[0] / calculateTotalXLoc(xlocs).getCodeLines() * 100);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
