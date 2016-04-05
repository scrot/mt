import distribution.ProjectStatistics;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args){
        String projectRoot = "C:\\Users\\royw\\Workspace\\junit4\\src\\main";
        Path path = FileSystems.getDefault().getPath(projectRoot);
        try {
            ProjectStatistics projectStatistics = new ProjectStatistics(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
