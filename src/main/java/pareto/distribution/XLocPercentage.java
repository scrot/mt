package pareto.distribution;

public class XLocPercentage {
    private final Percentage percentCode;
    private final Percentage percentComment;
    private final Percentage percentBlank;
    private final Percentage percentUnknown;

    public XLocPercentage(Percentage percentCode, Percentage percentComment, Percentage percentBlank, Percentage percentUnknown) {
        this.percentCode = percentCode;
        this.percentComment = percentComment;
        this.percentBlank = percentBlank;
        this.percentUnknown = percentUnknown;
    }

    public Percentage getPercentCode() {
        return percentCode;
    }

    public Percentage getPercentComment() {
        return percentComment;
    }

    public Percentage getPercentBlank() {
        return percentBlank;
    }

    public Percentage getPercentUnknown() {
        return percentUnknown;
    }

    @Override
    public String toString() {
        return this.percentCode + " code; " +
                this.percentComment + " comment; " +
                this.percentBlank + " blank; " +
                this.percentUnknown + " unknown.";
    }
}
