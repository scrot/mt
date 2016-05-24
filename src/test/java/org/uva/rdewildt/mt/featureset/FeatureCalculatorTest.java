package org.uva.rdewildt.mt.featureset;

import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
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
        Map<String, Feature> fs =  getFeatures("featuretests.zip", "featuretests.jar");
        assertEquals(1 ,fs.get("Main").getFaults());
        assertEquals(1 ,fs.get("Outer").getFaults());
        assertEquals(1 ,fs.get("Main$Inner").getFaults());
        assertEquals(1 ,fs.get("Outer$1").getFaults());
    }

    @Test
    public void testAuthors() {
        Map<String, Feature> fs =  getFeatures("featuretests.zip", "featuretests.jar");
        assertEquals(2 ,fs.get("Main").getAuthors());
        assertEquals(1 ,fs.get("Outer").getAuthors());
        assertEquals(1 ,fs.get("Main$Inner").getAuthors());
        assertEquals(1 ,fs.get("Outer$1").getAuthors());
    }

    @Test
    public void testChanges() {
        Map<String, Feature> fs =  getFeatures("featuretests.zip", "featuretests.jar");
        assertEquals(3 ,fs.get("Main").getChanges());
        assertEquals(2 ,fs.get("Outer").getChanges());
        assertEquals(2 ,fs.get("Main$Inner").getChanges());
        assertEquals(2 ,fs.get("Outer$1").getChanges());
    }

    @Test
    public void testAge() {
        Map<String, Feature> fs =  getFeatures("featuretests.zip", "featuretests.jar");
        assertEquals(1 ,fs.get("Main").getAge());
        assertEquals(0 ,fs.get("Outer").getAge());
        assertEquals(0 ,fs.get("Main$Inner").getAge());
        assertEquals(0 ,fs.get("Outer$1").getAge());
    }

    private Map<String, Feature> getFeatures(String zipRoot, String binaryRoot) {
        try {
            new ZipFile(getResource(zipRoot)).extractAll(getResource(zipRoot).getParent());
            Path sourcePath = Paths.get(getResource(zipRoot).getParent(), zipRoot.substring(0, zipRoot.lastIndexOf('.')));
            Path binaryPath = getResource(binaryRoot).toPath();
            FeatureCalculator mc = new FeatureCalculator(binaryPath, sourcePath);
            FileUtils.deleteDirectory(sourcePath.toFile());
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
