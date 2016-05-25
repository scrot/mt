package org.uva.rdewildt.mt.fpms;

import net.lingala.zip4j.core.ZipFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FeatureCalculatorTest {
    @Test
    public void testFaults() {
        String relativeBinary = Paths.get("build", "libs", "test-1.0-SNAPSHOT.jar").toString();
        Map<String, Feature> fs =  getFeatures("featuretests.zip", relativeBinary);
        assertEquals(1 ,fs.get("Main").getFaults());
        assertEquals(1 ,fs.get("Outer").getFaults());
        assertEquals(1 ,fs.get("Main$Inner").getFaults());
        assertEquals(1 ,fs.get("Outer$1").getFaults());
    }

    @Test
    public void testAuthors() {
        String relativeBinary = Paths.get("build", "libs", "test-1.0-SNAPSHOT.jar").toString();
        Map<String, Feature> fs =  getFeatures("featuretests.zip", relativeBinary);
        assertEquals(2 ,fs.get("Main").getAuthors());
        assertEquals(1 ,fs.get("Outer").getAuthors());
        assertEquals(1 ,fs.get("Main$Inner").getAuthors());
        assertEquals(1 ,fs.get("Outer$1").getAuthors());
    }

    @Test
    public void testChanges() {
        String relativeBinary = Paths.get("build", "libs", "test-1.0-SNAPSHOT.jar").toString();
        Map<String, Feature> fs =  getFeatures("featuretests.zip", relativeBinary);
        assertEquals(3 ,fs.get("Main").getChanges());
        assertEquals(2 ,fs.get("Outer").getChanges());
        assertEquals(2 ,fs.get("Main$Inner").getChanges());
        assertEquals(2 ,fs.get("Outer$1").getChanges());
    }

    @Test
    public void testAge() {
        String relativeBinary = Paths.get("build", "libs", "test-1.0-SNAPSHOT.jar").toString();
        Map<String, Feature> fs =  getFeatures("featuretests.zip", relativeBinary);
        assertEquals(1 ,fs.get("Main").getAge());
        assertEquals(0 ,fs.get("Outer").getAge());
        assertEquals(0 ,fs.get("Main$Inner").getAge());
        assertEquals(0 ,fs.get("Outer$1").getAge());
    }

    private Map<String, Feature> getFeatures(String zipRoot, String binaryRoot) {
        try {
            new ZipFile(getResource(zipRoot)).extractAll(getResource(zipRoot).getParent());
            Path sourcePath = Paths.get(getResource(zipRoot).getParent(), zipRoot.substring(0, zipRoot.lastIndexOf('.')));
            Path binaryPath = Paths.get(sourcePath.toString(), binaryRoot);
            FeatureCalculator mc = new FeatureCalculator(binaryPath, sourcePath);
            return mc.getFeatures();
        }
        catch (Exception e) { e.printStackTrace();}
        return null;
    }

    private File getResource(String name){
        try {
            Enumeration<URL> roots = ClassLoader.getSystemClassLoader().getResources(name);
            URL url = roots.nextElement();
            return new File(url.getPath());
        }
        catch (IOException e) {e.printStackTrace();}
        return null;
    }
}