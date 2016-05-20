package collector.model;

import collector.SourceCollector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static collector.SourceCollector.SourceFileReader;

public class ClassSource extends Source {
    private final String className;
    private final Path sourceFile;
    private final Path classFile;
    private final Location location;
    private final List<String> content;

    public ClassSource(String className, Path sourceFile, Path classFile, Location location) {
        this.className = className;
        this.sourceFile = sourceFile;
        this.classFile = classFile;
        this.location = location;
        this.content = collectContent();
    }

    public String getClassName() {
        return className;
    }

    @Override
    public Path getSourceFile() {
        return sourceFile;
    }

    public Path getClassFile() {
        return classFile;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public List<String> getContent() {
        return content;
    }

    public void removeInnerClasses(List<ClassSource> classes){
        for(ClassSource clazz : classes){
            Integer outerStart = this.getLocation().getStart().getLine();
            Integer outerEnd = this.getLocation().getEnd().getLine();
            Integer innerStart = clazz.getLocation().getStart().getLine();
            Integer innerEnd = clazz.getLocation().getEnd().getLine();
            if(innerStart > outerStart && innerEnd < outerEnd){
                for(int i = innerStart - 1; i < innerEnd; i++){
                    this.content.set(i, "");
                }
            }
        }
        this.content.removeAll(Arrays.asList("", null));
    }

    private List<String> collectContent() {
        List<String> content = new ArrayList<>();
        try {
            List<String> source = SourceFileReader(this.sourceFile);
            Integer start = this.location.getStart().getLine();
            Integer end = this.location.getEnd().getLine();
            for(int i = start - 1; i < end; i++){
                content.add(source.get(i));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
