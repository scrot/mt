import distribution.Loc;
import distribution.ProjectStatistics;
import distribution.language.structure.Language;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args){
        String projectRoot = "C:\\Users\\royw\\Workspace\\test1";
        Path path = FileSystems.getDefault().getPath(projectRoot);
        try {
            ProjectStatistics projectStatistics = new ProjectStatistics(path);
            Integer x  = projectStatistics.countClasses();
            Map<Language, Loc> y = projectStatistics.getLocPerLanguage();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
