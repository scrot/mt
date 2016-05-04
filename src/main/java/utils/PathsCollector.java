package utils;

import lang.Language;
import lang.LanguageFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathsCollector extends SimpleFileVisitor<Path> {
    private final Path sourcePath;
    private final List<Path> classPaths;
    private final List<Path> dirPaths;

    public PathsCollector(Path sourcePath) throws IOException {
        this.sourcePath = sourcePath;

        this.classPaths = new ArrayList<>();
        this.dirPaths = new ArrayList<>();

        Files.walkFileTree(this.sourcePath, this);
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

    public List<Path> collectClassPaths() {
        return this.classPaths;
    }

    public List<Path> collectClassPaths(Language ofLanguage){
        List<Path> filteredPaths = new ArrayList<>();
        for(Path classPath : this.classPaths){
            Language classLanguage = getLanguageFromClassPath(classPath);
            if(ofLanguage.equals(classLanguage)){
                filteredPaths.add(classPath);
            }
        }
        return filteredPaths;
    }

    public Map<Path, Language> collectClassPaths(List<Language> ofLanguages){
        Map<Path, Language> filteredClasses = new HashMap<>();
        for(Path classPath : this.classPaths){
            Language classLanguage = getLanguageFromClassPath(classPath);
            if(ofLanguages.contains(classLanguage)) {
                filteredClasses.put(classPath, classLanguage);
            }
        }
        return filteredClasses;
    }

    public static Language getLanguageFromClassPath(Path classPath){
        LanguageFactory factory = new LanguageFactory();
        return factory.classPathToLanguage(classPath);
    }


}
