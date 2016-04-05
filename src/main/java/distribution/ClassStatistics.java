package distribution;

import distribution.language.structure.Language;
import distribution.language.LanguageFactory;
import distribution.xloc.XLocCounter;
import distribution.xloc.XLocPatternFactory;

import java.nio.file.Path;

public class ClassStatistics {
    private final Path classPath;
    private final Language language;
    private final XLocCounter loc;

    public ClassStatistics(Path classPath) {
        this.classPath = classPath;
        this.language = getLanguageFromClassPath(classPath);

        XLocPatternFactory calculator = new XLocPatternFactory();
        this.loc = this.language.accept(calculator, this.classPath);

    }

    private Language getLanguageFromClassPath(Path classPath){
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }

    public Path getClassPath() {
        return classPath;
    }

    public Language getLanguage() {
        return language;
    }

    public XLocCounter getLoc() {
        return loc;
    }
}
