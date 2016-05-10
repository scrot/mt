package xloc;

import lang.Language;
import utils.SourceCollector;
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
import java.util.regex.Pattern;

/**
 * Created by roy on 4/5/16.
 */
public class XLocCalculator {
    private final Map<Path, XLoc> classXLocMap;
    private final Boolean ignoreGenerated;

    public XLocCalculator(Path rootPath, List<Language> languages, Boolean ignoreGenerated) throws IOException {

        Map<Path, Language> classPaths = new SourceCollector(rootPath).collectFilePaths(languages);
        this.classXLocMap = new HashMap<>();
        this.ignoreGenerated = ignoreGenerated;
        for(Map.Entry<Path, Language> classPath : classPaths.entrySet()){
            XLocPatternBuilder xLocPatterns = classPath.getValue().accept(new XLocPatternFactory(), null);
            List<String> classLines = mixedCharsetFileReader(classPath.getKey());
            XLoc xLoc = calculateClassXLoc(classLines, xLocPatterns, true);
            if(xLoc.getTotalLines() != 0){
                this.classXLocMap.put(rootPath.relativize(classPath.getKey()), xLoc);
            }
        }
    }

    public Map<Path, XLoc> getResult() {
        return this.classXLocMap;
    }

    private XLoc calculateClassXLoc(List<String> classLines, XLocPatternBuilder xLocPatterns, Boolean deduceCodeLine) {
        XLocCounter xLocCounter = new XLocCounter();
        if(ignoreGenerated && isGeneratedFile(classLines, xLocPatterns, 15)){
            return xLocCounter.getXLoc();
        }

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

    private Boolean isGeneratedFile(List<String> fileContent, XLocPatternBuilder xLocPatterns, Integer checkLines){
        Integer commentCount = 0;
        for(String line: fileContent){
            if(commentCount <= checkLines){
                if(xLocPatterns.isCommentLine(line)){
                    commentCount++;
                    if(Pattern.compile("(?i)generated").matcher(line).find()){
                        return true;
                    }
                }
            }
            else {
                return false;
            }
        }
        return false;
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
