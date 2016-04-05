package distribution.xloc;

import distribution.language.LanguageFactory;
import distribution.language.structure.Language;
import distribution.xloc.pattern.XLocPatternBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by roy on 4/5/16.
 */
public class XLocCalculator {
    private final XLoc xLoc;

    private final List<String> classLines;
    private final XLocPatternBuilder xLocPatterns;

    public XLocCalculator(Path classPath) throws IOException {
        Language classLanguage = getLanguageFromClassPath(classPath);

        this.classLines = Files.readAllLines(classPath);
        this.xLocPatterns = classLanguage.accept(new XLocPatternFactory(), null);

        this.xLoc = calculateClassXLoc();
    }

    public XLoc getResult() {
        return xLoc;
    }

    private XLoc calculateClassXLoc() {
        XLocCounter xLocCounter = new XLocCounter();
        for(String classLine : this.classLines){
            System.out.println(classLine);
            if(this.xLocPatterns.isBlankLine(classLine)){
                xLocCounter.incrementBlankLines();
            }
            if(this.xLocPatterns.isCommentLine(classLine)){
                xLocCounter.incrementCommentLines();
            }
            if(this.xLocPatterns.isCodeLine(classLine)){
                xLocCounter.incrementCodeLines();
            }
        }

        return xLocCounter.getXLoc();
    }

    private Language getLanguageFromClassPath(Path classPath){
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }
}
