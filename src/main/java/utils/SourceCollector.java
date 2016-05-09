package utils;

import lang.Language;
import lang.LanguageFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceCollector extends SimpleFileVisitor<Path> {
    private final List<Path> classPaths;
    private final List<Path> dirPaths;
    private final Boolean ignoreGenerated;

    public SourceCollector(Path sourcePath, Boolean ignoreGenerated) throws IOException {
        this.classPaths = new ArrayList<>();
        this.dirPaths = new ArrayList<>();
        this.ignoreGenerated = ignoreGenerated;

        Files.walkFileTree(sourcePath, this);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        this.dirPaths.add(dir);
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        this.classPaths.add(file);
        return super.visitFile(file, attrs);
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
                if(!ignoreGenerated && isGenerated(classPath) || ignoreGenerated)
                filteredClasses.put(classPath, classLanguage);
            }
        }
        return filteredClasses;
    }

    public static Language getLanguageFromClassPath(Path classPath){
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }

    private Boolean isGenerated(Path classPath) {
        try {
            FileReader reader = new FileReader(classPath.toFile());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
