package distribution;

import distribution.language.structure.Language;
import distribution.language.LanguageFactory;
import distribution.xloc.XLoc;
import distribution.xloc.XLocCalculator;

import java.io.IOException;
import java.nio.file.Path;

public class ClassStatistics {
    private final Path classPath;
    private final Language language;
    private final XLoc xLoc;

    public ClassStatistics(Path classPath) throws IOException {
        this.classPath = classPath;
        this.language = getLanguageFromClassPath(classPath);
        this.xLoc = new XLocCalculator(classPath).getResult();
    }

    public Path getClassPath() {
        return classPath;
    }

    public Language getLanguage() {
        return language;
    }

    public XLoc getXLoc() {
        return xLoc;
    }

    private Language getLanguageFromClassPath(Path classPath){
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }
}
