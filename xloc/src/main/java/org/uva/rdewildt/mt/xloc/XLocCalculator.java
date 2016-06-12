package org.uva.rdewildt.mt.xloc;

import org.uva.rdewildt.mt.utils.lang.Language;
import org.uva.rdewildt.mt.xloc.pattern.XLocPatternBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 4/5/16.
 */
public class XLocCalculator {
    private Map<Source, XLoc> xLocMap;

<<<<<<< HEAD
    public XLocCalculator(Path rootPath, Map<Language, List<Path>> includes) throws IOException {
        this.classXLocMap = new HashMap<>();
        for(Map.Entry<Language, List<Path>> entry : includes.entrySet()){
            XLocPatternBuilder xLocPatterns = entry.getKey().accept(new XLocPatternFactory(), null);
            for(Path classPath : entry.getValue()){
                List<String> classLines = mixedCharsetFileReader(classPath);
                XLoc xLoc = calculateClassXLoc(classLines, xLocPatterns);
                if(xLoc.getTotalLines() != 0){
                    this.classXLocMap.put(rootPath.relativize(classPath), xLoc);
                }
            }
        }
    }

    public XLocCalculator(Path rootPath, Boolean relativePaths, Boolean ignoreGenerated, Boolean ignoreTests, List<Language> languages) throws IOException {
        Map<Language, List<Path>> classPaths = new PathCollector(rootPath, relativePaths, ignoreGenerated, ignoreTests,languages).getFilePaths();
        this.classXLocMap = new HashMap<>();
        for(Map.Entry<Language, List<Path>> entry : classPaths.entrySet()){
            XLocPatternBuilder xLocPatterns = entry.getKey().accept(new XLocPatternFactory(), null);
            for(Path classPath : entry.getValue()){
                if(relativePaths){classPath = rootPath.resolve(classPath);}
                List<String> classLines = mixedCharsetFileReader(classPath);
                XLoc xLoc = calculateClassXLoc(classLines, xLocPatterns);
                if(xLoc.getTotalLines() != 0){
                    this.classXLocMap.put(rootPath.relativize(classPath), xLoc);
=======
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
>>>>>>> 43df69aa8be745b2f629fd8bc1665d7619432ca6
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

    private List<String> mixedCharsetFileReader(Path classpath) throws IOException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);

        FileInputStream stream = new FileInputStream(classpath.toFile());
        InputStreamReader reader = new InputStreamReader(stream, decoder);
        BufferedReader bufferedReader = new BufferedReader(reader);

        List<String> classLines = new ArrayList<>();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            classLines.add(line);
        }
        bufferedReader.close();

        return classLines;
    }
}
