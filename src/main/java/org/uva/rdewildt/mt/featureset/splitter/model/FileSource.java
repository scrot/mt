package org.uva.rdewildt.mt.featureset.splitter.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FileSource extends Source {
    private final Path sourceFile;
    private final List<String> content;

    public FileSource(Path sourceFile) throws IOException {
        this.sourceFile = sourceFile;
        this.content = SourceFileReader(sourceFile);
    }

    @Override
    public Path getSourceFile() {
        return this.sourceFile;
    }

    @Override
    public List<String> getContent() {
        return this.content;
    }
}