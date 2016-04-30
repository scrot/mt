import com.messners.gitlab.api.GitLabApiException;
import git.project.Project;
import report.ConfigReader;
import report.Report;
import report.ReportBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
 * TODO: Split crawler into CommitCrawler, IssueCrawler etc.
 * TODO: Improve crawling speed -> crawl local Commits
 * TODO: Use of codeGini -> Remove it...
 * TODO: CodeDistr of the x% most faulty modules
 * TODO: Caching commits, issues, etc.
 * TODO: Verify/test lines of code counting
 */
public class Main {
    public static void main(String[] args) throws IOException, GitLabApiException {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("/home/roy/Workspace/MT/mt/src/main/resources/example.conf");
        ConfigReader confReader = new ConfigReader(config);
        List<Project> projects = confReader.getProjects();
        writeSimpleReportFile(confReader.getName(), projects, " ");
    }

    public static void writeSimpleReportFile(String filename, List<Project> projects, String seperator) throws IOException, GitLabApiException {
        FileWriter writer = new FileWriter(filename + ".sdf");
        writer.write("");
        writer.close();

        if(!projects.isEmpty()){
            Boolean setHeader = false;
            for(Project project : projects){
                writer = new FileWriter(filename + ".sdf", true);
                System.out.println("Building Report " + (projects.indexOf(project) + 1) + "/" + projects.size() + "...");
                ReportBuilder report = new ReportBuilder(project);
                if(!setHeader){
                    writer.write(String.join(seperator, report.simpleReport().keySet()) + '\n');
                    setHeader = true;
                }
                writer.write(String.join(seperator, report.simpleReport().values()) + '\n');
                writer.close();
            }

        }
    }
}
