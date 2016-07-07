package org.uva.rdewildt.mt.ovms.distribution;

import org.uva.rdewildt.mt.utils.model.Percentage;

import java.util.*;
import java.util.stream.Collectors;

public class Distribution {
    private final Map<Integer, Integer> distribution;

    public <T> Distribution(Map<T, Integer> counts) {
        this.distribution = buildCumulativeDistribution(counts);
    }

    public Double giniCoefficient() {
        Double linSpace = 0.5 * this.cumulativeHeadOfPartition(new Percentage(100.0)) * this.distribution.size();
        Double lorenzSpace = 0.0;
        for (Integer i = 1; i <= this.distribution.size(); i++) {
            if (i == 1) {
                lorenzSpace += 0.5 * this.distribution.get(i).doubleValue();
            } else {
                lorenzSpace += 0.5 * (this.distribution.get(i).doubleValue() - this.distribution.get(i - 1)) + this.distribution.get(i - 1);
            }
        }
        return (linSpace - lorenzSpace) / linSpace;
    }

    public Integer cumulativeHeadOfPartition(Percentage partition) {
        Integer index = getDistributionIndex(partition);

        if (index == 0) {
            return 0;
        } else {
            return distribution.get(index);
        }
    }

    public Integer cumulativeTailOfPartition(Percentage partition) {
        Integer startIndex = getDistributionIndex(new Percentage(100.0 - partition.getPercentage()));
        Integer endIndex = getDistributionIndex(new Percentage(100.0));

        if (startIndex == 0) {
            return distribution.get(endIndex);
        } else {
            Integer start = distribution.get(startIndex);
            Integer end = distribution.get(endIndex);
            return end - start;
        }
    }

    public Percentage cumulativeHeadOfPartitionPercentage(Percentage partition) {
        Integer total = cumulativeHeadOfPartition(new Percentage(100.0));
        Integer value = cumulativeHeadOfPartition(partition);
        return new Percentage(percentageOf(value, total).getPercentage());
    }

    public Percentage cumulativeTailOfPartitionPercentage(Percentage partition) {
        Integer total = cumulativeTailOfPartition(new Percentage(100.0));
        Integer value = cumulativeTailOfPartition(partition);
        return new Percentage(percentageOf(value, total).getPercentage());
    }

    public List<Percentage> plotCodeDistribution(Double start, Double end, Double interval) {
        List<Percentage> plot = new ArrayList<>();
        for (Double i = end; i >= start; i -= interval) {
            Percentage partition = new Percentage(i);
            plot.add(this.cumulativeHeadOfPartitionPercentage(partition));
        }
        return plot;
    }

    private Integer getDistributionIndex(Percentage partition) {
        Double classes = (this.distribution.size() * partition.getPercentage()) / 100;
        return classes.intValue();
    }

    //TODO: Track cause of divide by zero when not checking total < 1
    private Percentage percentageOf(Integer value, Integer total) {
        if (total < 1) {
            return new Percentage(0.0);
        }
        return new Percentage((double) (value * 100 / total));
    }

    private <T> Map<Integer, Integer> buildCumulativeDistribution(Map<T, Integer> counts) {
        Map<Integer, Integer> distributionMap = new LinkedHashMap<>();
        List<Integer> values = new ArrayList<>(counts.values()).stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());


        Integer moduleCount = 0;
        Integer cumulativeTotal = 0;
        for (Integer value : values) {
            moduleCount++;
            cumulativeTotal += value;
            distributionMap.put(moduleCount, cumulativeTotal);
        }

        return distributionMap;
    }

}
