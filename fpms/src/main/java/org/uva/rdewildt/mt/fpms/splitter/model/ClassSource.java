package org.uva.rdewildt.mt.fpms.splitter.model;

import org.uva.rdewildt.mt.xloc.PathCollector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassSource implements Source {
    private final String className;
    private final Path sourceFile;
    private final Path classFile;
    private final Location location;

    public ClassSource(String className, Path sourceFile, Path classFile, Location location) {
        this.className = className;
        this.sourceFile = sourceFile;
        this.classFile = classFile;
        this.location = location;
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

    /*
    public void collectContent(Map<String, ClassSource> innerClasses) {
        List<String> content = new ArrayList<>();
        try {
            List<String> source = PathCollector.mixedCharsetFileReader(this.sourceFile);
            Integer start = this.location.getStart().getLine();
            Integer end = this.location.getEnd().getLine();
            for(int i = start; i <= end; i++){
                if(!innerClass(i, new ArrayList<>(innerClasses.values()))){
                    content.add(source.get(i - 1));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.content = content;
    }
    */

    private Boolean innerClass(Integer index, List<ClassSource> innerClasses){
        for(ClassSource innerClass : innerClasses){
            Integer innerStart = innerClass.getLocation().getStart().getLine();
            Integer innerEnd = innerClass.getLocation().getEnd().getLine();
            Integer thisStart = this.getLocation().getStart().getLine();
            Integer thisEnd = this.getLocation().getEnd().getLine();
            if(innerStart > thisStart && innerEnd < thisEnd && index >= innerStart && index <= innerEnd){
                return true;
            }
        }

        return false;
    }
}