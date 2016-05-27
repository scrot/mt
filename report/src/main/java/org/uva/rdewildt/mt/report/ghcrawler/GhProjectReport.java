package org.uva.rdewildt.mt.report.ghcrawler;

import org.uva.rdewildt.mt.lims.Report;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectReport extends Report {
    public GhProjectReport(String name) {
        super(name, new GhProject());
    }
}
