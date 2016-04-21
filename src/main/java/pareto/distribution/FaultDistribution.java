package pareto.distribution;

import faults.crawler.GHFaultCrawler;
import faults.crawler.GLFaultCrawler;
import faults.fault.GHFault;
import faults.fault.GLFault;
import faults.repository.GHRepoBuilder;
import faults.repository.GLRepoBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FaultDistribution implements Distribution {
    private final FaultDistributionValue distribution;

    public FaultDistribution(Path rootPath, String ghRepositoryName) throws IOException {
        GHRepoBuilder repositoryBuilder = new GHRepoBuilder(ghRepositoryName,
                "9db4058bee86c76f1769f03c6cf37d7ac9c2f1b0");
        GHFaultCrawler creator = new GHFaultCrawler(repositoryBuilder.getRepository(), rootPath);
        Map<Path, List<GHFault>> classFaults = creator.getClassFaults();
        this.distribution = new FaultDistributionValue(buildCumulativeDistributionMap(classFaults));
    }

    public FaultDistribution(Path rootPath, String glDomainURL, Integer glProjectID) throws IOException {
        GLRepoBuilder repoBuilder = new GLRepoBuilder(glDomainURL,
                "1-MjVfz8NREu-7mRgxsk");
        GLFaultCrawler creator = new GLFaultCrawler(repoBuilder.getRepository(glProjectID), glProjectID, rootPath);
        Map<Path, List<GLFault>> classFaults = creator.getClassFaults();
        this.distribution = null;//new FaultDistributionValue(buildCumulativeDistributionMap(classFaults));
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

    private Map<Integer, Integer> buildCumulativeDistributionMap(Map<Path, List<GHFault>> classFaultsMap){
        Map<Integer, Integer> distributionMap = new HashMap<>();

        List<Integer> sortedFaultSizes = new ArrayList<>();
        for(List<GHFault> faults : classFaultsMap.values()){
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

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
    {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(e -> e.getValue()))
                .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));

        return result;
    }
}
