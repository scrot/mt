package distribution.xloc;

import distribution.language.LanguageFactory;
import distribution.language.structure.Language;
import distribution.xloc.pattern.XLocPatternBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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


        this.classLines = mixedCharsetFileReader(classPath);
        this.xLocPatterns = classLanguage.accept(new XLocPatternFactory(), null);

        this.xLoc = calculateClassXLoc(true);
    }

    public XLoc getResult() {
        return xLoc;
    }

    private XLoc calculateClassXLoc(Boolean deduceCodeLine) {
        XLocCounter xLocCounter = new XLocCounter();
        for(String classLine : this.classLines){

            Boolean blankline = this.xLocPatterns.isBlankLine(classLine);
            Boolean commentline = this.xLocPatterns.isCommentLine(classLine);
            Boolean codeline = this.xLocPatterns.isCodeLine(classLine);
            Boolean unknownline = this.xLocPatterns.isUnknownLine(classLine);
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

    private Language getLanguageFromClassPath(Path classPath){
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }
}
