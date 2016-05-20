package report;

import collector.SourceCollector;
import com.messners.gitlab.api.GitLabApiException;
import gitcrawler.model.Project;
import lang.Java;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReportWriter {
    public static void main(String[] args) throws IOException, GitLabApiException, GitAPIException, ClassNotFoundException {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("C:\\Users\\royw\\Workspace\\fm-toolkit\\src\\main\\resources\\example.conf");
        ConfigReader confReader = new ConfigReader(config);
        List<Project> projects = confReader.getProjects();
        //OverviewBuilder builder = new OverviewBuilder(confReader.getName(), projects);
        //builder.writeOverviewReportToFile(", ");
        SourceCollector collector = new SourceCollector(projects.get(0).getLocalPath(), new Java(), true, true);
        FeatureBuilder fbuilder = new FeatureBuilder(projects);
        fbuilder.writeOverviewReportToFile(", ");
    }
}
