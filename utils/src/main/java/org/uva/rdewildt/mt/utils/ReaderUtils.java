package org.uva.rdewildt.mt.utils;

import org.uva.rdewildt.mt.utils.model.lang.Language;
import org.uva.rdewildt.mt.utils.model.lang.LanguageFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 7/7/16.
 */
public class ReaderUtils {
    public static List<String> mixedCharsetFileReader(Path file) {
        return mixedCharsetFileReader(file, Integer.MAX_VALUE);
    }

    public static List<String> mixedCharsetFileReader(Path filePath, Integer limit) {
        File file = filePath.toFile();

        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);

        try (FileInputStream stream = new FileInputStream(file)) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, decoder))) {
                List<String> classLines = new ArrayList<>();

                int i = 0;
                String line;
                while ((line = bufferedReader.readLine()) != null && i < limit) {
                    classLines.add(line);
                    i++;
                }
                return classLines;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static Language getLanguageFromClassPath(Path classPath) {
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }
}
