package pareto.distribution;

import cfaults.CFaultsCreator;
import cfaults.Fault;
import cfaults.GHRepositoryBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class FaultDistribution implements Distribution {
    private final FaultDistributionValue distribution;

    public FaultDistribution(Path rootPath, String repositoryName) throws IOException {
        GHRepositoryBuilder repositoryBuilder = new GHRepositoryBuilder(repositoryName,
                "9db4058bee86c76f1769f03c6cf37d7ac9c2f1b0");
        CFaultsCreator creator = new CFaultsCreator(repositoryBuilder.getRepository(), rootPath);
        Map<Path, List<Fault>> classFaults = creator.getClassFaults();
        this.distribution = new FaultDistributionValue(buildCumulativeDistributionMap(classFaults));
    }

    @Override
    public Integer cumulativeOfPartition(Percentage partition) {
        return this.distribution.cumulativeOfPartition(partition);
    }

    @Override
    public Percentage cumulativePercentageOfPartition(Percentage partition) {
        return this.distribution.cumulativeOfPartitionPercentage(partition);
    }

    @Override
    public List<Percentage> plotCodeDistribution(Double start, Double end, Double interval) {
        List<Percentage> plot = new ArrayList<>();
        for(Double i = start; i <= end; i += interval){
            Percentage partition = new Percentage(i);
            plot.add(this.distribution.cumulativeOfPartitionPercentage(partition));
        }
        return plot;
    }

    private Map<Integer, Integer> buildCumulativeDistributionMap(Map<Path, List<Fault>> classFaultsMap){
        Map<Integer, Integer> distributionMap = new HashMap<>();

        List<Integer> sortedFaultSizes = new ArrayList<>();
        for(List<Fault> faults : classFaultsMap.values()){
            sortedFaultSizes.add(faults.size());
        }
        sortedFaultSizes.sort(Collections.reverseOrder());

        Integer moduleCount = 0;
        Integer cumulativeTotal = 0;
        for (Integer faultSize : sortedFaultSizes){
            moduleCount++;
            cumulativeTotal = cumulativeTotal + faultSize;
            distributionMap.put(moduleCount, cumulativeTotal);
        }

        return distributionMap;
    }
}
