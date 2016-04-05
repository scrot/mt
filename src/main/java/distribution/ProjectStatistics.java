package distribution;

import distribution.language.structure.Language;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ProjectStatistics {
    private final Path sourcePath;
    private final List<Path> classPaths;

    private final Integer moduleCount;
    private final Integer classCount;

    private final Map<Language, Integer> locPerLangType;
    private final Integer totalLoc;

    public ProjectStatistics(Path sourcePath) throws IOException {
        this.sourcePath = sourcePath;
        this.classPaths = collectClassPaths(sourcePath);

        this.moduleCount = countModules(sourcePath);
        this.classCount = countClasses(this.classPaths);

        this.locPerLangType = countLocPerLangType(sourcePath);
        this.totalLoc = calculateTotalLoc(sourcePath);
    }

    private Integer calculateTotalLoc(Path projectPath) {
        return null;
    }

    private List<Path> collectClassPaths(Path projectPath) throws IOException {
        return new PathsCollector(projectPath).collectClassPaths();
    }

    private Integer countClasses(List<Path> classPaths) {
        return classPaths.size();
    }

    private Integer countModules(Path projectPath) {
        return null;
    }

    private Map<Language, Integer> countLocPerLangType(Path projectPath) {
        return null;
    }
}
