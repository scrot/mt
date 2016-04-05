package distribution;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

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

    public List<Path> collectClassPaths() {
        return classPaths;
    }

    public List<Path> collectDirPaths() {
        return dirPaths;
    }
}
