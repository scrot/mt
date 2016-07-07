package org.uva.rdewildt.mt.fpms;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FeatureCalculatorTest {
    @Test
    public void testFaults() {
        Map<String, Feature> fs = getFeatures("featuretests.zip");
        assertEquals(1, fs.get("Main").getFaults());
        assertEquals(1, fs.get("Outer").getFaults());
        assertEquals(1, fs.get("Main$Inner").getFaults());
        assertEquals(1, fs.get("Outer$1").getFaults());
    }

    @Test
    public void testAuthors() {
        Map<String, Feature> fs = getFeatures("featuretests.zip");
        assertEquals(2, fs.get("Main").getAuthors());
        assertEquals(1, fs.get("Outer").getAuthors());
        assertEquals(1, fs.get("Main$Inner").getAuthors());
        assertEquals(1, fs.get("Outer$1").getAuthors());
    }

    @Test
    public void testChanges() {
        Map<String, Feature> fs = getFeatures("featuretests.zip");
        assertEquals(3, fs.get("Main").getChanges());
        assertEquals(2, fs.get("Outer").getChanges());
        assertEquals(2, fs.get("Main$Inner").getChanges());
        assertEquals(2, fs.get("Outer$1").getChanges());
    }

    @Test
    public void testAge() {
        Map<String, Feature> fs = getFeatures("featuretests.zip");
        assertEquals(1, fs.get("Main").getAge());
        assertEquals(0, fs.get("Outer").getAge());
        assertEquals(0, fs.get("Main$Inner").getAge());
        assertEquals(0, fs.get("Outer$1").getAge());
    }

    private Map<String, Feature> getFeatures(String zipRoot) {
        Map<String, Feature> mc = new HashMap<>();
        Path sourcePath = Paths.get(getResource(zipRoot).getParent(), zipRoot.substring(0, zipRoot.lastIndexOf('.')));

        try {
            unzip(getResource(zipRoot).toString(), getResource(zipRoot).getParent());
            mc = new FeatureCalculator(sourcePath, sourcePath, true, false, false).getFeatures();
            FileUtils.deleteDirectory(sourcePath.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mc;
    }

    private File getResource(String name) {
        try {
            Enumeration<URL> roots = ClassLoader.getSystemClassLoader().getResources(name);
            URL url = roots.nextElement();
            return new File(url.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void unzip(String zipFilename, String destDirname) throws IOException {

        final Path destDir = Paths.get(destDirname);
        if (Files.notExists(destDir)) {
            Files.createDirectories(destDir);
        }

        try (FileSystem zipFileSystem = createZipFileSystem(zipFilename, false)) {
            final Path root = zipFileSystem.getPath("/");

            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs) throws IOException {
                    final Path destFile = Paths.get(destDir.toString(),
                            file.toString());
                    Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir,
                                                         BasicFileAttributes attrs) throws IOException {
                    final Path dirToCreate = Paths.get(destDir.toString(),
                            dir.toString());
                    if (Files.notExists(dirToCreate)) {
                        Files.createDirectory(dirToCreate);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    private FileSystem createZipFileSystem(String zipFilename, boolean create) throws IOException {
        // convert the filename to a URI
        final Path path = Paths.get(zipFilename);
        final URI uri = URI.create("jar:file:" + path.toUri().getPath());

        final Map<String, String> env = new HashMap<>();
        if (create) {
            env.put("create", "true");
        }
        return FileSystems.newFileSystem(uri, env);
    }
}