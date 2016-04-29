package distr;

import distr.model.CodeDistributionValue;
import distr.model.Percentage;
import distr.model.XLocPercentage;
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
    public Double giniCoefficient() {
        Double gini = 0.0;
        Map<Integer, Integer> codeDistribution = this.distribution.getDistribution();
        Map<Integer, Double> linearDistribution = buildLinearDistribution(distribution.size(), distribution.cumulativeOfPartition(new Percentage(100.0)));

        for(Integer i = 0; i < codeDistribution.size(); i++){
            Double value = linearDistribution.get(i) - (double)(codeDistribution.get(i));
            if(value > 0){
                gini += value;
            }
        }
        return gini;
    }

    @Override
    public Integer cumulativeOfPartition(Percentage partition){
        return this.distribution.cumulativeOfPartition(partition);
    }

    @Override
    public Percentage cumulativePercentageOfPartition(Percentage partition){
        return this.distribution.cumulativeOfPartitionPercentage(partition);
    }

    @Override
    public List<Percentage> plotCodeDistribution(Double start, Double end, Double interval){
        List<Percentage> plot = new ArrayList<>();
        for(Double i = end; i >= start; i -= interval){
            Percentage partition = new Percentage(i);
            plot.add(this.distribution.cumulativeOfPartitionPercentage(partition));
        }
        return plot;
    }

    private Map<Integer, Double>  buildLinearDistribution(Integer totalX, Integer totalFiles){
        Double avgX = (double) (totalX / totalFiles);
        Map<Integer, Double> linearDistribution = new LinkedHashMap<>();
        for(Integer i = 0 ; i < totalFiles; i++){
            linearDistribution.put(i, avgX + linearDistribution.get(i-1));
        }
        return linearDistribution;
    }

    private Map<Integer, Integer> buildCumulativeDistributionMap(Map<Path, XLoc> classXLocMap){
        Map<Integer, Integer> distributionMap = new LinkedHashMap<>();

        List<XLoc> sortedXLocs = new ArrayList<>(classXLocMap.values());
        sortedXLocs.sort(Comparator.reverseOrder());

        Integer moduleCount = 0;
        Integer cumulativeTotal = 0;
        for (XLoc xLoc : sortedXLocs){
            moduleCount++;
            cumulativeTotal += xLoc.getCodeLines();
            distributionMap.put(moduleCount, cumulativeTotal);
        }

        return distributionMap;
    }
}
