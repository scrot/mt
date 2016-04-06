package pareto;

import language.Language;
import language.LanguageFactory;

import java.io.IOException;
import java.nio.file.Path;

public class ClassStatistics {
    private final Path classPath;
    private final Language language;

    public ClassStatistics(Path classPath) throws IOException {
        this.classPath = classPath;
        this.language = getLanguageFromClassPath(classPath);
    }

    public Path getClassPath() {
        return classPath;
    }

    public Language getLanguage() {
        return language;
    }

    private Language getLanguageFromClassPath(Path classPath){
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }
}
