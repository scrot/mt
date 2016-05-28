package org.uva.rdewildt.mt.lims;

import org.uva.rdewildt.mt.report.Report;

/**
 * Created by roy on 5/23/16.
 */
public class MetricReport extends Report {

    public MetricReport(String name) {
        super(name, new Metric());
    }
}
