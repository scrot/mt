import com.messners.gitlab.api.GitLabApiException;
import git.model.Project;
import org.eclipse.jgit.api.errors.GitAPIException;
import report.ConfigReader;
import report.ReportBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/*
 * TODO: CodeDistr of the x% most faulty modules
 * TODO: Filter faults in files not in xloc (normalize after?)
 * TODO: Relative path for xloc (normalize after)
 */
public class Main {
    public static void main(String[] args) throws IOException, GitLabApiException, GitAPIException {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("/home/roy/Workspace/MT/mt/src/main/resources/example.conf");
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
