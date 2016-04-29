package distr;

import distr.model.Percentage;

public interface Distribution {

    Object cumulativeOfPartition(Percentage partition);
    Object cumulativePercentageOfPartition(Percentage partition);
    Object plotCodeDistribution(Double start, Double end, Double interval);
}