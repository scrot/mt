package pareto.distribution;

import xloc.XLoc;

import java.nio.file.Path;
import java.util.Map;

public class FaultDistribution extends Distribution {
    private final Map<Integer, XLoc> distributionMap;

    public FaultDistribution(Path rootPath){

        this.distributionMap = buildCumulativeDistributionMap(classFaultMap);
    }
}
