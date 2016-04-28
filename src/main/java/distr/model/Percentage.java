package distr.model;

public class Percentage {
    private final Double percentage;

    public Percentage(Double percentage){
        if(percentage >= 0 && percentage <= 100){
            this.percentage = percentage;
        }
        else {
            throw new IllegalArgumentException();
        }
        assert this.percentage >= 0 && this.percentage <= 100;
    }

    public Double getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return percentage + "%";
    }
}
