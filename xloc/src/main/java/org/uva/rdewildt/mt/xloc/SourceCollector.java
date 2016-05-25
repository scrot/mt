package org.uva.rdewildt.mt.xloc;

import org.uva.rdewildt.mt.xloc.lang.Language;
import org.uva.rdewildt.mt.xloc.lang.LanguageFactory;
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

public class SourceCollector extends SimpleFileVisitor<Path> {
    private final List<Path> classPaths;
    private final List<Path> dirPaths;
    private final Boolean ignoreGenerated;
    private final Boolean ignoreTests;

    public SourceCollector(Path sourcePath, Boolean ignoreGenerated, Boolean ignoreTests) throws IOException {
        this.classPaths = new ArrayList<>();
        this.dirPaths = new ArrayList<>();
        this.ignoreGenerated = ignoreGenerated;
        this.ignoreTests = ignoreTests;

        Files.walkFileTree(sourcePath, this);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        this.dirPaths.add(dir);
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if(!isGeneratedFile(file) && !isTestFile(file)){
            this.classPaths.add(file);
        }
        return super.visitFile(file, attrs);
    }

    public static Language getLanguageFromClassPath(Path classPath) {
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }

    public static List<String> mixedCharsetFileReader(Path classpath) throws IOException {
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

    public List<Path> collectDirPaths() {
        return this.dirPaths;
    }

    public List<Path> collectFilePaths() {
        return this.classPaths;
    }

    public List<Path> collectFilePaths(Language ofLanguage){
        List<Path> filteredPaths = new ArrayList<>();
        for(Path classPath : this.classPaths){
            Language classLanguage = getLanguageFromClassPath(classPath);
            if(ofLanguage.equals(classLanguage)){
                filteredPaths.add(classPath);
            }
        }
        return filteredPaths;
    }

    public Map<Path, Language> collectFilePaths(List<Language> ofLanguages){
        Map<Path, Language> filteredClasses = new HashMap<>();
        for(Path classPath : this.classPaths){
            Language classLanguage = getLanguageFromClassPath(classPath);
            if(ofLanguages.contains(classLanguage)) {
                filteredClasses.put(classPath, classLanguage);
            }
        }
        return filteredClasses;
    }

    private Boolean isGeneratedFile(Path file) throws IOException {
        if(ignoreGenerated){
            Integer commentCount = 0;
            List<String> fileContent = mixedCharsetFileReader(file);
            XLocPatternBuilder xLocPatterns = getLanguageFromClassPath(file).accept(new XLocPatternFactory(), null);

            for(String line: fileContent){
                if(commentCount <= 15){
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
        else {
            return false;
        }
    }

    //TODO: use visitor if more languages are supported
    private Boolean isTestFile(Path file) throws IOException {
        if(this.ignoreTests){
            String fileContent = String.join(" ", mixedCharsetFileReader(file));
            return Pattern.compile("(?m)@(Test|Before|After)").matcher(fileContent).find();
        }
        else {
            return false;
        }
    }
}
