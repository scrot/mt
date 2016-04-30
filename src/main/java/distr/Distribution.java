package distr;

import java.util.*;
import java.util.stream.Collectors;

public class Distribution {
    private final Map<Integer, Integer> distribution;

    public <T> Distribution(Map<T, Integer> counts){
        this.distribution = buildCumulativeDistribution(counts);
    }

    public Double giniCoefficient() {

        Map<Integer, Double> linearDistribution = buildLinearDistribution(
                this.distribution.size(),
                this.cumulativeOfPartition(new Percentage(100.0))
        );

        Double totalSpace = 0.5 * this.cumulativeOfPartition(new Percentage(100.0)) * linearDistribution.size();
        Double diffSpace = 0.0;
        for(Integer i = 1; i <= this.distribution.size(); i++){
            Double linSpace, actSpace;
            if(i == 1) {
                linSpace = 0.5 * linearDistribution.get(i);
                actSpace = 0.5 * this.distribution.get(i).doubleValue();
            }
            else {
                linSpace = 0.5 * linearDistribution.get(i) + linearDistribution.get(i-1);
                actSpace = 0.5 * this.distribution.get(i).doubleValue() + this.distribution.get(i-1);
            }
            Double value = linSpace - actSpace;
            assert(value >= 0);
            diffSpace += value;
        }
        assert(totalSpace > diffSpace);
        Double gini = diffSpace / totalSpace;
        assert(gini >= 0.0 && gini <= 1.0);
        return gini;
    }

    public Integer cumulativeOfPartition(Percentage partition){
        Integer index = getDistributionIndex(partition);

        if(index == 0){
            return 0;
        }
        else {
            return distribution.get(index);
        }
    }

    public Percentage cumulativeOfPartitionPercentage(Percentage partition) {
        Integer total = cumulativeOfPartition(new Percentage(100.0));
        Integer value = cumulativeOfPartition(partition);
        return new Percentage(percentageOf(value, total).getPercentage());
    }

    public List<Percentage> plotCodeDistribution(Double start, Double end, Double interval) {
        List<Percentage> plot = new ArrayList<>();
        for(Double i = end; i >= start; i -= interval){
            Percentage partition = new Percentage(i);
            plot.add(this.cumulativeOfPartitionPercentage(partition));
        }
        return plot;
    }

    private Integer getDistributionIndex(Percentage partition) {
        Double classes = (this.distribution.size() * partition.getPercentage())/100;
        return classes.intValue();
    }

    private Percentage percentageOf(Integer value, Integer total){
        return new Percentage((double) (value * 100 / total));
    }

    private Map<Integer, Double>  buildLinearDistribution(Integer totalElements, Integer totalCount){
        Double avg = totalCount.doubleValue() / totalElements.doubleValue();
        Map<Integer, Double> linearDistribution = new HashMap<>();
        Double cummulative = 0.0;
        for(Integer i = 1 ; i <= totalElements; i++){
            cummulative += avg;
            linearDistribution.put(i, cummulative);
        }
        return linearDistribution;
    }

    private <T> Map<Integer, Integer> buildCumulativeDistribution(Map<T, Integer> counts){
        Map<Integer, Integer> distributionMap = new LinkedHashMap<>();
        List<Integer> values = new ArrayList<>(counts.values()).stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());


        Integer moduleCount = 0;
        Integer cumulativeTotal = 0;
        for (Integer value : values){
            moduleCount++;
            cumulativeTotal += value;
            distributionMap.put(moduleCount, cumulativeTotal);
        }

        return distributionMap;
    }

}
