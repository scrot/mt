import distribution.xloc.XLoc;
import distribution.xloc.XLocCounter;
import distribution.ProjectStatistics;
import distribution.language.structure.Language;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args){
        String projectRoot = "C:\\Users\\royw\\Workspace\\junit4\\src\\main\\java";
        Path path = FileSystems.getDefault().getPath(projectRoot);
        try {
            ProjectStatistics projectStatistics = new ProjectStatistics(path);
            Integer x  = projectStatistics.countClasses();
            Map<Language, XLoc> y = projectStatistics.getXLocPerLanguage();
            System.out.println();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
