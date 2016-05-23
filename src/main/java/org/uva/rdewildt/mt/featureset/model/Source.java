package org.uva.rdewildt.mt.featureset.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class Source {
    public abstract Path getSourceFile();
    public abstract List<String> getContent();

    public static List<String> SourceFileReader(Path classpath) throws IOException {
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
