package pareto;

import language.Language;
import xloc.XLoc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectStatistics {
    private final Path sourcePath;
    private final List<Path> classPaths;
    private final List<ClassStatistics> classStatistics;

    public ProjectStatistics(Path sourcePath) throws IOException {
        this.sourcePath = sourcePath;
        this.classPaths = collectClassPaths(sourcePath);
        this.classStatistics = buildClassStatistics();
    }

    public Integer countClasses() {
        return classPaths.size();
    }


    private List<ClassStatistics> buildClassStatistics() throws IOException {
        List<ClassStatistics> classStatistics = new ArrayList<>();
        for(Path classPath : this.classPaths){
            classStatistics.add(new ClassStatistics(classPath));
        }
        return classStatistics;
    }

    private List<Path> collectClassPaths(Path projectPath) throws IOException {
        return new PathsCollector(projectPath).collectClassPaths();
    }


}
