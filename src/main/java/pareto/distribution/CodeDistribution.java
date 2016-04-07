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
        Integer index = getDistributionIndex(distribution);

        if(index == 0){
            return new XLoc(0,0,0,0);
        }
        else {
            return this.distributionMap.get(index);
        }
    }

    public XLocPercentage cumulativeXLocPercentageOfPartition(Percentage distribution){
        XLoc xLocTotal = cumulativeXLocOfPartition(new Percentage(100.0));
        XLoc xLocValue = cumulativeXLocOfPartition(distribution);
        CodeDistributionValue cdValue = new CodeDistributionValue(
                distribution,
                this.distributionMap.size(),
                xLocValue,
                xLocTotal);
        return cdValue.getCummulativeXLocPercentage();
    }

    public List<CodeDistributionValue> plotCodeDistribution(Double start, Double end, Double interval){
        List<CodeDistributionValue> plot = new ArrayList<>();
        for(Double i = start; i <= end; i += interval){
            Percentage partition = new Percentage(i);
            plot.add(new CodeDistributionValue(
                    partition,
                    getDistributionIndex(partition),
                    cumulativeXLocOfPartition(partition),
                    cumulativeXLocOfPartition(new Percentage(100.0))
            ));
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

    private Integer getDistributionIndex(Percentage partition) {
        Double classes = (distributionMap.size() * partition.getPercentage())/100;
        return classes.intValue();
    }
}
