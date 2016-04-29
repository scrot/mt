package distr;

import distr.model.DistributionValue;
import distr.model.Percentage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Distribution {
    private DistributionValue distribution;

    

    public abstract Percentage cumulativeOfPartition(Percentage partition);
    public abstract Percentage cumulativePercentageOfPartition(Percentage partition);
    public abstract List<Percentage> plotCodeDistribution(Double start, Double end, Double interval);

    public Double giniCoefficient() {
        Double gini = 0.0;
        Map<Integer, Integer> faultDistribution = this.distribution.getDistribution();
        Map<Integer, Double> linearDistribution = buildLinearDistribution(distribution.size(), distribution.cumulativeOfPartition(new Percentage(100.0)));

        for(Integer i = 0; i < faultDistribution.size(); i++){
            Double value = linearDistribution.get(i) - (double)(faultDistribution.get(i));
            if(value > 0){
                gini += value;
            }
        }
        return gini;
    }

    private Map<Integer, Double>  buildLinearDistribution(Integer totalX, Integer totalFiles){
        Double avgX = (double) (totalX / totalFiles);
        Map<Integer, Double> linearDistribution = new LinkedHashMap<>();
        for(Integer i = 0 ; i < totalFiles; i++){
            linearDistribution.put(i, avgX + linearDistribution.get(i-1));
        }
        return linearDistribution;
    }

}
