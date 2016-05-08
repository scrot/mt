package report;

import com.messners.gitlab.api.GitLabApiException;
import git.model.Project;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/*
 * TODO: Boost IssueCrawling (other lib?)
 */
public class ReportWriter {
    public static void main(String[] args) throws IOException, GitLabApiException, GitAPIException {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("/home/roy/Workspace/MT/mt/src/main/resources/example.conf");
        ConfigReader confReader = new ConfigReader(config);
        List<Project> projects = confReader.getProjects();
        OverviewBuilder builder = new OverviewBuilder(confReader.getName(), projects);
        builder.writeOverviewReportToFile(", ");
        //FeatureBuilder fbuilder = new FeatureBuilder(projects);
        //fbuilder.writeOverviewReportToFile(", ");
    }
}
