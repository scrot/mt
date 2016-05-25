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
    private Map<Source, XLoc> xLocMap;

    public enum Level{ FILE, CLASS}
    public XLocCalculator(Path rootPath, Language language, Level level) throws IOException {
        if(level == Level.FILE){
            List<Path> filePaths = new SourceCollector(rootPath, language, true, true).getFilePaths();
            this.xLocMap = new HashMap<>();
            for(Path filePath : filePaths){
                XLocPatternBuilder xLocPatterns = language.accept(new XLocPatternFactory(), null);
                Source file = new FileSource(filePath);
                XLoc xLoc = calculateClassXLoc(file.getContent(), xLocPatterns);
                this.xLocMap.put(file, xLoc);
            }
        }
        else if(level == Level.CLASS){
            List<ClassSource> classes = new SourceCollector(rootPath, language, true, true).getClassSources();
            this.xLocMap = new HashMap<>();
            for(ClassSource clazz : classes){
                XLocPatternBuilder xLocPatterns = language.accept(new XLocPatternFactory(), null);
                XLoc xLoc = calculateClassXLoc(clazz.getContent(), xLocPatterns);
                if(xLoc.getTotalLines() != 0){
                    this.xLocMap.put(clazz, xLoc);
                }
            }
        }
    }

    public Map<Source, XLoc> getResult() {
        return this.xLocMap;
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
