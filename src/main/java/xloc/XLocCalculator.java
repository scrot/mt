package xloc;

import lang.Language;
import utils.PathsCollector;
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

    public XLocCalculator(Path rootPath, List<Language> languages) throws IOException {

        Map<Path, Language> classPaths = new PathsCollector(rootPath).collectClassPaths(languages);
        this.classXLocMap = new HashMap<>();
        for(Map.Entry<Path, Language> classPath : classPaths.entrySet()){
            XLocPatternBuilder xLocPatterns = classPath.getValue().accept(new XLocPatternFactory(), null);
            List<String> classLines = mixedCharsetFileReader(classPath.getKey());
            XLoc xLoc = calculateClassXLoc(classLines, xLocPatterns, true);
            this.classXLocMap.put(rootPath.relativize(classPath.getKey()), xLoc);
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
}
