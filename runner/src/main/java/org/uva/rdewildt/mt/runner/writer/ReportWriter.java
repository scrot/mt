package org.uva.rdewildt.mt.runner.writer;

import org.uva.rdewildt.mt.gcrawler.git.GitUtils;
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
        Path top1000 = Paths.get("/home/roy/Workspace/MT/mt/dataset/original/top1000.csv");
        Report top1000Report = new GhReport(top1000);

        //List<String> names = top1000Report.getReport().get("Name").stream().map(Object::toString).collect(Collectors.toList());
        //List<String> groups = top1000Report.getReport().get("Group").stream().map(Object::toString).collect(Collectors.toList());
        //List<Path> paths = top1000Report.getReport().get("GitPath").stream().map(x -> Paths.get(x.toString())).collect(Collectors.toList());
        //OverviewReportBuilder obuilder = new OverviewReportBuilder("top1000", MapUtils.listsToKeyValueMap(names, paths), true, true);
        //obuilder.writeReportsToFile(top1000);
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
