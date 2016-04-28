package distr.model;

import java.util.Map;

public class FaultDistributionValue implements DistributionValue {
    private final Map<Integer, Integer> distributionMap;

    public FaultDistributionValue(Map<Integer, Integer> distributionMap) {
        this.distributionMap = distributionMap;
    }

    @Override
    public Integer cumulativeOfPartition(Percentage partition) {
        Integer index = getDistributionIndex(partition);

        if(index == 0){
            return 0;
        }
        else {
            return this.distributionMap.get(index);
        }
    }

    @Override
    public Percentage cumulativeOfPartitionPercentage(Percentage partition) {
        Integer total = cumulativeOfPartition(new Percentage(100.0));
        Integer value = cumulativeOfPartition(partition);
        return new Percentage(percentageOf(value, total).getPercentage());
    }

    private Integer getDistributionIndex(Percentage partition) {
        Double classes = (distributionMap.size() * partition.getPercentage())/100;
        return classes.intValue();
    }

    private Percentage percentageOf(Integer value, Integer total){
        return new Percentage((double) (value * 100 / total));
    }

}
