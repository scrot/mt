package org.uva.rdewildt.mt.lims;

/**
 * Created by roy on 5/23/16.
 */
public class MetricReport extends Report {

    public MetricReport(String name) {
        super(name, Metric.getMetricNames());
    }
}
