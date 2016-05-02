package git.model;

import java.nio.file.Path;

/**
 * Created by roy on 5/2/16.
 */
public class FPath {
    private final Path path;
    private final Integer faultCount;

    public FPath(Path path, Integer faultCount) {
        this.path = path;
        this.faultCount = faultCount;
    }

    public Path getPath() {
        return path;
    }

    public Integer getFaultCount() {
        return faultCount;
    }
}
