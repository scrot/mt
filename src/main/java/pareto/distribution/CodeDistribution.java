package pareto.distribution;

import xloc.XLoc;
import xloc.XLocCalculator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class CodeDistribution extends Distribution {
    private final Map<Integer, XLoc> distributionMap;

    public CodeDistribution(Path rootPath) throws IOException {
        Map<Path, XLoc> classXLocMap = new XLocCalculator(rootPath).getResult();
        this.distributionMap = buildCumulativeDistributionMap(classXLocMap);
    }

    public XLoc cumulativeXLocOfPartition(Percentage distribution){
        Double index = (distributionMap.size() * distribution.getPercentage())/100;
        Integer roundedIndex = index.intValue();
        return this.distributionMap.get(roundedIndex);
    }

    private Map<Integer, XLoc> buildCumulativeDistributionMap(Map<Path, XLoc> classXLocMap){
        Map<Integer, XLoc> distributionMap = new HashMap<>();

        List<XLoc> sortedXLocs = new ArrayList<>(classXLocMap.values());
        sortedXLocs.sort(Collections.reverseOrder());

        Integer moduleCount = 0;
        XLoc cumulativeTotal = new XLoc(0,0,0,0);
        distributionMap.put(moduleCount, cumulativeTotal);
        for (XLoc xLoc : sortedXLocs){
            moduleCount++;
            cumulativeTotal = cumulativeTotal.add(xLoc);
            distributionMap.put(moduleCount, cumulativeTotal);
        }

        return distributionMap;
    }
}
