package org.uva.rdewildt.mt.fpms.splitter.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FileSource implements Source {
    private final Path sourceFile;

    public FileSource(Path sourceFile) throws IOException {
        this.sourceFile = sourceFile;
    }

    @Override
    public Path getSourceFile() {
        return this.sourceFile;
    }
}