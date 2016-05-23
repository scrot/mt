package org.uva.rdewildt.mt.featureset;

import org.uva.rdewildt.lims.Report;

/**
 * Created by roy on 5/23/16.
 */
public class FeatureReport extends Report {
    public FeatureReport(String name) {
        super(name, Feature.getFeatureNames());
    }
}
