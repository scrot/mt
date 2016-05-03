import com.messners.gitlab.api.GitLabApiException;
import git.model.Project;
import org.eclipse.jgit.api.errors.GitAPIException;
import report.ConfigReader;
import report.OverviewBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/*
 * TODO: Boost IssueCrawling (other lib?)
 */
public class Main {
    public static void main(String[] args) throws IOException, GitLabApiException, GitAPIException {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("C:\\Users\\royw\\Workspace\\mt\\src\\main\\resources\\example.conf");
        ConfigReader confReader = new ConfigReader(config);
        List<Project> projects = confReader.getProjects();
        writeSimpleReportFile(confReader.getName(), projects, ", ");
    }

    public static void writeSimpleReportFile(String filename, List<Project> projects, String seperator) throws IOException, GitLabApiException, GitAPIException {
        FileWriter writer = new FileWriter(filename + ".csv");
        writer.write("");
        writer.close();

        if(!projects.isEmpty()){
            Boolean setHeader = false;
            for(Project project : projects){
                writer = new FileWriter(filename + ".csv", true);
                System.out.println("Building Report " + (projects.indexOf(project) + 1) + "/" + projects.size() + "...");
                OverviewBuilder report = new OverviewBuilder(project);
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
