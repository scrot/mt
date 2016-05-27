package collector.model;

import java.nio.file.Path;
import java.util.List;

public abstract class Source {
    public abstract Path getSourceFile();
    public abstract List<String> getContent();
}
