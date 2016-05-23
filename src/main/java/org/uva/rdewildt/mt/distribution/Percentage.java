package org.uva.rdewildt.mt.distribution;

public class Percentage {
    private final Double percentage;

    public Percentage(Double percentage){
        assert percentage >= 0 && percentage <= 100;
        this.percentage = percentage;
    }

    public Double getPercentage0to1() { return percentage / 100; }

    public Double getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return percentage + "%";
    }
}
