package distr.model;

import java.util.Map;

public interface DistributionValue {
    Integer size();
    Map<Integer, Integer> getDistribution();
    Object cumulativeOfPartition(Percentage distribution);
    Object cumulativeOfPartitionPercentage(Percentage distribution);
}
