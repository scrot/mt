package distr.model;

import xloc.XLoc;

import java.util.Map;

public class CodeDistributionValue implements DistributionValue {
    private final Map<Integer, XLoc> distributionMap;

    public CodeDistributionValue(Map<Integer, XLoc> distributionMap) {
        this.distributionMap = distributionMap;
    }

    @Override
    public XLoc cumulativeOfPartition(Percentage partition){
        Integer index = getDistributionIndex(partition);

        if(index == 0){
            return new XLoc(0,0,0,0);
        }
        else {
            return this.distributionMap.get(index);
        }
    }

    @Override
    public XLocPercentage cumulativeOfPartitionPercentage(Percentage partition){
        XLoc total = cumulativeOfPartition(new Percentage(100.0));
        XLoc value = cumulativeOfPartition(partition);
        return new XLocPercentage(
                percentageOf(value.getCodeLines(), total.getCodeLines()),
                percentageOf(value.getCommentLines(), total.getCommentLines()),
                percentageOf(value.getBlankLines(), total.getBlankLines()),
                percentageOf(value.getUnknownLines(), total.getUnknownLines())
        );
    }

    private Integer getDistributionIndex(Percentage partition) {
        Double classes = (distributionMap.size() * partition.getPercentage())/100;
        return classes.intValue();
    }

    private Percentage percentageOf(Integer value, Integer total){
        if(total == 0){
            return new Percentage(0.0);
        }
        return new Percentage((double) (value * 100 / total));
    }
}
