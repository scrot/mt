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
import java.util.*;
import java.util.regex.Pattern;

public class SourceCollector extends SimpleFileVisitor<Path> {
    private final List<ClassSource> classSources;
    private final List<Path> filePaths;
    private final List<Path> dirPaths;

    private final Language ofLanguage;
    private final Boolean ignoreGenerated;
    private final Boolean ignoreTests;

    public SourceCollector(Path sourcePath, Language ofLanguage, Boolean ignoreGenerated, Boolean ignoreTests) throws IOException {
        this.classSources = new ArrayList<>();
        this.filePaths = new ArrayList<>();
        this.dirPaths = new ArrayList<>();

        this.ofLanguage = ofLanguage;
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
        if(isOfLanguage(file) && !isGeneratedFile(file) && !isTestFile(file)){
            this.filePaths.add(file);
            this.classSources.addAll(new ClassBaseVisitor(file).getClassSources());
        }
        return super.visitFile(file, attrs);
    }

    public static Language getLanguageFromClassPath(Path classPath) {
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }

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

    public List<Path> getDirPaths() {
        return this.dirPaths;
    }

    public List<Path> getFilePaths() {
        return this.filePaths;
    }

    public List<ClassSource> getClassSources() { return this.classSources; }

    private Boolean isGeneratedFile(Path file) throws IOException {
        if(ignoreGenerated){
            Integer commentCount = 0;
            List<String> fileContent = SourceFileReader(file);
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

    private Boolean isTestFile(Path file) throws IOException {
        if(this.ignoreTests){
            String fileContent = String.join(" ", SourceFileReader(file));
            return Pattern.compile("(?m)@(Test|Before|After)").matcher(fileContent).find();
        }
        else {
            return false;
        }
    }

    private Boolean isOfLanguage(Path file) {
        Language classLanguage = getLanguageFromClassPath(file);
        if(this.ofLanguage.equals(classLanguage)) {
            return true;
        }
        else {
            return  false;
        }
    }
}
