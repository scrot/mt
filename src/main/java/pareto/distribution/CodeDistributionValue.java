package pareto.distribution;

import xloc.XLoc;

public class CodeDistributionValue extends Distribution {
    private final Percentage partition;
    private final Integer classCount;
    private final XLoc cummulativeXLoc;
    private final XLocPercentage cummulativeXLocPercentage;
    private final XLoc totalXLoc;

    public CodeDistributionValue(Percentage partition, Integer classCount, XLoc cummulativeXLoc, XLoc totalXLoc) {
        this.partition = partition;
        this.classCount = classCount;
        this.totalXLoc = totalXLoc;
        this.cummulativeXLoc = cummulativeXLoc;
        this.cummulativeXLocPercentage = cumulativeXLocPercentage();
    }

    public Integer getClassCount() {
        return classCount;
    }

    public Percentage getPartition() {
        return partition;
    }

    public XLoc getCummulativeXLoc() {
        return cummulativeXLoc;
    }

    public XLocPercentage getCummulativeXLocPercentage() {
        return cummulativeXLocPercentage;
    }

    public XLoc getTotalXLoc() {
        return totalXLoc;
    }

    private XLocPercentage cumulativeXLocPercentage(){
        return new XLocPercentage(
                percentageOf(this.cummulativeXLoc.getCodeLines(), this.totalXLoc.getCodeLines()),
                percentageOf(this.cummulativeXLoc.getCommentLines(), this.totalXLoc.getCommentLines()),
                percentageOf(this.cummulativeXLoc.getBlankLines(), this.totalXLoc.getBlankLines()),
                percentageOf(this.cummulativeXLoc.getUnknownLines(), this.totalXLoc.getUnknownLines())
        );
    }

    private Percentage percentageOf(Integer value, Integer total){
        return new Percentage((double) (value * 100 / total));
    }
}
