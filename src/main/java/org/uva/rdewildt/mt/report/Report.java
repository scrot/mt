package org.uva.rdewildt.mt.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.uva.rdewildt.mt.utils.Utils.transposeValues;

public class Report {
    private final String name;
    private final Map<String, List<String>> report;

    public Report(String name, Map<String, List<String>> report) {
        this.name = name;
        this.report = report;
    }

    public String getName() {
        return name;
    }

    public Map<String, List<String>> getReport() {
        return report;
    }

    public List<String> getHeader(){
        return new ArrayList<>(report.keySet());
    }

    public List<List<String>> getBody(){
        return transposeValues(this.report);
    }
}
