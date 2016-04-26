package xloc;

import lang.Language;
import lang.LanguageFactory;
import distr.PathsCollector;
import xloc.pattern.XLocPatternBuilder;

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
    private final Map<Path, XLoc> classXLocMap;

    public XLocCalculator(Path rootPath) throws IOException {

        List<Path> classPaths = collectClassPaths(rootPath);

        this.classXLocMap = new HashMap<>();
        for(Path classPath : classPaths){
            Language classLanguage = getLanguageFromClassPath(classPath);
            XLocPatternBuilder xLocPatterns = classLanguage.accept(new XLocPatternFactory(), null);
            List<String> classLines = mixedCharsetFileReader(classPath);
            XLoc xLoc = calculateClassXLoc(classLines, xLocPatterns, true);
            this.classXLocMap.put(classPath, xLoc);
        }
    }

    public Map<Path, XLoc> getResult() {
        return this.classXLocMap;
    }

    private XLoc calculateClassXLoc(List<String> classLines, XLocPatternBuilder xLocPatterns, Boolean deduceCodeLine) {
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

            if(commentline){
                xLocCounter.incrementCommentLines();
            }

            if(deduceCodeLine){
                if(!(blankline || commentline || unknownline)){
                    xLocCounter.incrementCodeLines();
                }
            }
            else {
                if (codeline) {
                    xLocCounter.incrementCodeLines();
                }
            }

            if(unknownline){
                xLocCounter.incrementUnknownLines();
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

    private List<Path> collectClassPaths(Path projectPath) throws IOException {
        return new PathsCollector(projectPath).collectClassPaths();
    }

    private Language getLanguageFromClassPath(Path classPath){
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }
}
