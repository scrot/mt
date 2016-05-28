package org.uva.rdewildt.mt.gcrawler.github;

import org.uva.rdewildt.mt.report.Report;

/**
 * Created by roy on 5/27/16.
 */
public class GhProjectReport extends Report {
    public GhProjectReport(String name) {
        super(name, new GhProject());
    }
}
