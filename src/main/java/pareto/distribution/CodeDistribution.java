package pareto.distribution;

import xloc.XLoc;
import xloc.XLocCalculator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class CodeDistribution implements Distribution {
    private final CodeDistributionValue distribution;

    public CodeDistribution(Path rootPath) throws IOException {
        Map<Path, XLoc> classXLocMap = new XLocCalculator(rootPath).getResult();
        this.distribution = new CodeDistributionValue(buildCumulativeDistributionMap(classXLocMap));
    }

    @Override
    public XLoc cumulativeOfPartition(Percentage partition){
        return this.distribution.cumulativeOfPartition(partition);
    }

    @Override
    public XLocPercentage cumulativePercentageOfPartition(Percentage partition){
        return this.distribution.cumulativeOfPartitionPercentage(partition);
    }

    @Override
    public List<XLocPercentage> plotCodeDistribution(Double start, Double end, Double interval){
        List<XLocPercentage> plot = new ArrayList<>();
        for(Double i = start; i <= end; i += interval){
            Percentage partition = new Percentage(i);
            plot.add(this.distribution.cumulativeOfPartitionPercentage(partition));
        }
        return plot;
    }

    private Map<Integer, XLoc> buildCumulativeDistributionMap(Map<Path, XLoc> classXLocMap){
        Map<Integer, XLoc> distributionMap = new HashMap<>();

        List<XLoc> sortedXLocs = new ArrayList<>(classXLocMap.values());
        sortedXLocs.sort(Collections.reverseOrder());

        Integer moduleCount = 0;
        XLoc cumulativeTotal = new XLoc(0,0,0,0);
        for (XLoc xLoc : sortedXLocs){
            moduleCount++;
            cumulativeTotal = cumulativeTotal.add(xLoc);
            distributionMap.put(moduleCount, cumulativeTotal);
        }

        return distributionMap;
    }
}
