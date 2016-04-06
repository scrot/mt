package distribution;

import distribution.language.structure.Language;
import distribution.xloc.XLoc;
import distribution.xloc.XLocCounter;

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

    public Map<Language, XLoc> getXLocPerLanguage(){
        Map<Language, XLoc> map = new HashMap<>();

        for (ClassStatistics classStatistic : classStatistics){
            if(!map.containsKey(classStatistic.getLanguage())){
                map.put(classStatistic.getLanguage(), classStatistic.getXLoc());
            }
            else {
                XLoc currentTotalXLoc = map.get(classStatistic.getLanguage());
                XLoc currentClassXLoc = classStatistic.getXLoc();
                map.put(classStatistic.getLanguage(), currentTotalXLoc.add(currentClassXLoc));
            }
        }

        return map;
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
