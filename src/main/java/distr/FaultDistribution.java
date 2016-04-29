package distr;

import com.messners.gitlab.api.GitLabApiException;
import distr.model.FaultDistributionValue;
import distr.model.Percentage;
import git.model.Fault;
import git.project.Project;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FaultDistribution implements Distribution {
    private final FaultDistributionValue distribution;

    public FaultDistribution(Project project, Path rootPath) throws GitLabApiException {
        Map<Path, List<Fault>> classFaults = project.getGitCrawler().getFaults();
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
        for(Double i = end; i >= start; i -= interval){
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
        sortedFaultSizes.sort(Comparator.reverseOrder());

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
