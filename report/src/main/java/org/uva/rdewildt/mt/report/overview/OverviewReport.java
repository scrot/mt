package org.uva.rdewildt.mt.report.overview;

import org.uva.rdewildt.mt.lims.Report;

/**
 * Created by roy on 5/26/16.
 */
public class OverviewReport extends Report{
    public OverviewReport(String name) {
        super(name, new Overview());
    }
}
