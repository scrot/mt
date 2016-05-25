package org.uva.rdewildt.mt.xloc;

import org.uva.rdewildt.mt.xloc.lang.Language;
import org.uva.rdewildt.mt.xloc.pattern.XLocPatternBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.uva.rdewildt.mt.xloc.SourceCollector.mixedCharsetFileReader;

/**
 * Created by roy on 4/5/16.
 */
public class XLocCalculator {
    private final Map<Path, XLoc> classXLocMap;

    public XLocCalculator(Path rootPath, List<Language> languages) throws IOException {

        Map<Path, Language> classPaths = new SourceCollector(rootPath, true, true).collectFilePaths(languages);
        this.classXLocMap = new HashMap<>();
        for(Map.Entry<Path, Language> classPath : classPaths.entrySet()){
            XLocPatternBuilder xLocPatterns = classPath.getValue().accept(new XLocPatternFactory(), null);
            List<String> classLines = mixedCharsetFileReader(classPath.getKey());
            XLoc xLoc = calculateClassXLoc(classLines, xLocPatterns);
            if(xLoc.getTotalLines() != 0){
                this.classXLocMap.put(rootPath.relativize(classPath.getKey()), xLoc);
            }
        }
    }

    public Map<Path, XLoc> getResult() {
        return this.classXLocMap;
    }

    private XLoc calculateClassXLoc(List<String> classLines, XLocPatternBuilder xLocPatterns) {
        XLocCounter xLocCounter = new XLocCounter();

        for(String classLine : classLines){

            Boolean blankline = xLocPatterns.isBlankLine(classLine);
            Boolean commentline = xLocPatterns.isCommentLine(classLine);
            Boolean codeline = xLocPatterns.isCodeLine(classLine);
            Boolean unknownline = xLocPatterns.isUnknownLine(classLine);
            assert blankline || commentline || codeline || unknownline;

            if(blankline){
                xLocCounter.incrementBlankLines();
            }
            else if(commentline){
                xLocCounter.incrementCommentLines();
            }
            else if(unknownline){
                xLocCounter.incrementUnknownLines();
            }
            else {
                xLocCounter.incrementCodeLines();
            }
        }

        return xLocCounter.getXLoc();
    }
}
