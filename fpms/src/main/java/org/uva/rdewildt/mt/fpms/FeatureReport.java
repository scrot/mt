package org.uva.rdewildt.mt.fpms;

import org.uva.rdewildt.mt.lims.Report;

/**
 * Created by roy on 5/23/16.
 */
public class FeatureReport extends Report {
    public FeatureReport(String name) {
        super(name, new Feature());
    }
}
