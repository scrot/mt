package org.uva.rdewildt.mt.utils;

import org.uva.rdewildt.mt.utils.lang.Language;
import org.uva.rdewildt.mt.utils.lang.LanguageFactory;
import org.uva.rdewildt.mt.xloc.XLocPatternFactory;
import org.uva.rdewildt.mt.xloc.pattern.XLocPatternBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PathCollector extends SimpleFileVisitor<Path> {
    private final Map<Language, List<Path>> classPaths;

    private final Path sourcePath;
    private final Boolean relativePaths;
    private final Boolean ignoreGenerated;
    private final Boolean ignoreTests;
    private final List<Language> ofLanguage;

    public PathCollector(Path sourcePath, Boolean relativePaths, Boolean ignoreGenerated, Boolean ignoreTests, List<Language> ofLanguage) {
        this.classPaths = new HashMap<>();

        this.sourcePath = sourcePath;
        this.relativePaths = relativePaths;
        this.ignoreGenerated = ignoreGenerated;
        this.ignoreTests = ignoreTests;
        this.ofLanguage = ofLanguage;

        try {
            Files.walkFileTree(sourcePath, this);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (isOfLanguage(file) && !isGeneratedFile(file) && !isTestFile(file)) {
            if (this.relativePaths) {
                addValueToMapList(this.classPaths, getLanguageFromClassPath(file), this.sourcePath.relativize(file));
            } else {
                addValueToMapList(this.classPaths, getLanguageFromClassPath(file), file);
            }
        }
        return super.visitFile(file, attrs);
    }

    public Map<Language, List<Path>> getFilePaths() {
        return this.classPaths;
    }

    private Boolean isGeneratedFile(Path file) throws IOException {
        if(ignoreGenerated){
            XLocPatternBuilder xLocPatterns = getLanguageFromClassPath(file).accept(new XLocPatternFactory(), null);
            List<String> lines = mixedCharsetFileReader(file, 15);
            return lines.stream().anyMatch( line -> xLocPatterns.isCommentLine(line) && Pattern.compile("(?i)generated").matcher(line).find());
        }
        return false;
    }

    private Boolean isTestFile(Path file) throws IOException {
        if(this.ignoreTests){
            return file.toString().contains("test") ||
                    mixedCharsetFileReader(file,30).stream().anyMatch(line -> Pattern.compile("(?mi)@(Test|Before|After)").matcher(line).find());
        }
        return false;
    }

    private Boolean isOfLanguage(Path file){
        if(this.ofLanguage.contains(getLanguageFromClassPath(file))){
            return true;
        }
        else {
            return false;
        }
    }

    private List<String> mixedCharsetFileReader(Path file, Integer limit) throws IOException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);

        FileInputStream stream = new FileInputStream(file.toFile());
        InputStreamReader reader = new InputStreamReader(stream, decoder);
        BufferedReader bufferedReader = new BufferedReader(reader);

        List<String> classLines = new ArrayList<>();

        int i = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null && i < limit) {
            classLines.add(line);
            i++;
        }
        bufferedReader.close();

        return classLines;
    }

    private Language getLanguageFromClassPath(Path classPath) {
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }

    private <K, V> void addValueToMapList(Map<K, List<V>> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<V>() {{ add(value); }});
        }
        else {
            List<V> newvalue = map.get(key);
            newvalue.add(value);
            map.put(key, newvalue);
        }
    }
}
