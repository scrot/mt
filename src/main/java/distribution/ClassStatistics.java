package distribution;

import distribution.language.structure.Language;
import distribution.language.LanguageFactory;

import java.nio.file.Path;

public class ClassStatistics {
    private final Path classPath;
    private final Language language;
    private final Integer loc;

    public ClassStatistics(Path classPath) {
        this.classPath = classPath;
        this.language = getLanguageFromClassPath(classPath);

    }

    private Language getLanguageFromClassPath(Path classPath){
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }

}
