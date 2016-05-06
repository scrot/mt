package utils;

import lang.Language;
import lang.LanguageFactory;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 5/6/16.
 */
public class ClassCollector extends SimpleFileVisitor<Path> {
    private final List<Path> classPaths;
    private final List<Path> dirPaths;

    public ClassCollector(Path sourcePath) throws IOException {
        this.classPaths = new ArrayList<>();
        this.dirPaths = new ArrayList<>();
        try (FileSystem fs = FileSystems.newFileSystem(sourcePath, null)){
            Path walkerRoot = fs.getPath("/");
            Files.walkFileTree(walkerRoot, this);
        } catch (Exception e){
            e.getStackTrace();
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        this.dirPaths.add(dir);
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if(FilenameUtils.getExtension(file.toString()).equals("class")){
            this.classPaths.add(file);
        }
        return super.visitFile(file, attrs);
    }

    public List<Path> collectDirPaths() {
        return this.dirPaths;
    }

    public List<Path> collectClassPaths() {
        return this.classPaths;
    }
}
