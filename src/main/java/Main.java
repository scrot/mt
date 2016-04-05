import distribution.xloc.XLocCounter;
import distribution.ProjectStatistics;
import distribution.language.structure.Language;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args){
        String projectRoot = "/home/roy/Workspace/MT/junit4/src/main/java";
        Path path = FileSystems.getDefault().getPath(projectRoot);
        try {
            ProjectStatistics projectStatistics = new ProjectStatistics(path);
            Integer x  = projectStatistics.countClasses();
            Map<Language, XLocCounter> y = projectStatistics.getLocPerLanguage();
            System.out.println();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
