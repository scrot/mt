package pareto.distribution;

public interface DistributionValue {
    Object cumulativeOfPartition(Percentage distribution);
    Object cumulativeOfPartitionPercentage(Percentage distribution);
}
