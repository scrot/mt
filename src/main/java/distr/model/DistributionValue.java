package distr.model;

public interface DistributionValue {
    Object cumulativeOfPartition(Percentage distribution);
    Object cumulativeOfPartitionPercentage(Percentage distribution);
}
