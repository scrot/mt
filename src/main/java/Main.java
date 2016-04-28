import com.messners.gitlab.api.GitLabApiException;
import git.model.Project;
import report.ConfigReader;
import report.ReportBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, GitLabApiException {
        //Path config = Paths.get(args[0]);
        Path config = Paths.get("C:\\Users\\royw\\Workspace\\mt\\src\\main\\resources\\example.conf");
        ConfigReader confReader = new ConfigReader(config);
        List<Project> projects = confReader.getProjects();

        //Path projectPath = Paths.get(args[1]);
        Path projectPath = Paths.get("C:\\Users\\royw\\Workspace\\test");

        List<ReportBuilder> reports = new ArrayList<>();
        for(Project project : projects){
            reports.add(new ReportBuilder(project, projectPath));
        }
        writeBasicInformationToSDF(reports);
    }

    public static void writeBasicInformationToSDF(List<ReportBuilder> reports) throws IOException {
        FileWriter writer = new FileWriter("example.sdf");

        writer.write("#Days #Files #SLOC\n");

        for(ReportBuilder report : reports){
            writer.write(report.getProjectDevelopmentDaysCount() + " ");
            writer.write(report.getProjectFilesCount() + " ");
            writer.write(report.getProjectCodeCount() + "\n");
        }

        writer.close();
    }
}
