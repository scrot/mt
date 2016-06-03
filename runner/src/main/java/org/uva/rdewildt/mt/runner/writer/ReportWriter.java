package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.gcrawler.github.GhReport;
import org.uva.rdewildt.mt.report.Report;
import org.uva.rdewildt.mt.utils.MapUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportWriter {
    public static void main(String[] args) throws Exception {
        Path top1000 = Paths.get("C:\\Users\\royw\\Workspace\\top1000");
        GhBuilder(top1000);
        Report top1000Report = new GhReport(Paths.get(top1000.toString(), "top1000.csv"));

        List<String> names = top1000Report.getReport().get("Name").stream().map(Object::toString).collect(Collectors.toList());
        List<Path> paths = top1000Report.getReport().get("GitPath").stream().map(x -> Paths.get(x.toString())).collect(Collectors.toList());
        OverviewReportBuilder obuilder = new OverviewReportBuilder("top1000", MapUtils.listsToKeyValueMap(names, paths), true, true);
        obuilder.writeReportsToFile(top1000);
        //FeatureReportsBuilder fbuilder = new FeatureReportsBuilder(projects, true, true);
        //fbuilder.writeReportsToFile();
    }

    private static void GhBuilder(Path top1000) {
        //GhBuilder
        Map<String, String> params = new HashMap<String, String>(){{put("language", "Java");}};
        GhProjectReportsBuilder ghbuilder = new GhProjectReportsBuilder("top1000", 1000, params, top1000);
        ghbuilder.writeReportsToFile(top1000);
    }
}
