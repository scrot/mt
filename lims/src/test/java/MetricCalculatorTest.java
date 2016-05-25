import org.junit.Test;
import org.uva.rdewildt.mt.lims.Metric;
import org.uva.rdewildt.mt.lims.MetricCalculator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by roy on 5/21/16.
 */
public class MetricCalculatorTest {

    @Test
    public void testWmc(){
        Map<String, Metric> ms = getMetrics("lims-test-0.1.jar");
        assertEquals(16, ms.get("WmcTest1").getWmc());
    }

    @Test
    public void testNoc() {
        Map<String, Metric> ms = getMetrics("lims-test-0.1.jar");
        assertEquals(3, ms.get("NocTest1$Super").getNoc());
        assertEquals(2, ms.get("NocTest1$Child1").getNoc());
        assertEquals(0, ms.get("NocTest1$Child2").getNoc());
        assertEquals(0, ms.get("NocTest1$Child3").getNoc());
        assertEquals(0, ms.get("NocTest1$Child11").getNoc());
        assertEquals(0, ms.get("NocTest1$Child12").getNoc());
    }

    @Test
    public void testRfc() {
        Map<String, Metric> ms = getMetrics("lims-test-0.1.jar");
        assertEquals(7, ms.get("RfcTest1$Main").getRfc());
        assertEquals(3, ms.get("RfcTest1$ExternClass1").getRfc());
    }

    @Test
    public void testCbo(){
        Map<String, Metric> ms = getMetrics("lims-test-0.1.jar");
        assertEquals(9, ms.get("CboTest1$Main1").getCbo());
        assertEquals(5, ms.get("CboTest1$Main2").getCbo());
        assertEquals(4, ms.get("CboTest1$Couple1").getCbo());
        assertEquals(3, ms.get("CboTest1$Couple2").getCbo());
        assertEquals(4, ms.get("CboTest1$Couple3").getCbo());
        assertEquals(2, ms.get("CboTest1$InnerCouple1").getCbo());
    }

    @Test
    public void testDit() {
        Map<String, Metric> ms = getMetrics("lims-test-0.1.jar");
        assertEquals(1, ms.get("DitTest1$Super").getDit());
        assertEquals(2, ms.get("DitTest1$Child").getDit());
        assertEquals(3, ms.get("DitTest1$Infant").getDit());
        assertEquals(1, ms.get("DitTest1$Interface").getDit());
        assertEquals(1, ms.get("DitTest1$Implementation").getDit());
        assertEquals(4, ms.get("DitTest1$NewList").getDit());

    }

    @Test
    public void testLcom() {
        Map<String, Metric> ms = getMetrics("lims-test-0.1.jar");
        assertEquals(6, ms.get("LcomTest1$Lcom1").getLcom());
        assertEquals(0, ms.get("LcomTest1$Lcom2").getLcom());
        assertEquals(0, ms.get("LcomTest1$Lcom3").getLcom());
        assertEquals(0, ms.get("LcomTest1$Lcom4").getLcom());
    }

    @Test
    public void testMpc() {
        Map<String, Metric> ms = getMetrics("lims-test-0.1.jar");
        assertEquals(7, ms.get("MpcTest1$Mpc1").getMpc());
        assertEquals(1, ms.get("MpcTest1$Mpc2").getMpc());
        assertEquals(1, ms.get("MpcTest1$Mpc3").getMpc());

    }

    @Test
    public void testDac() {
        Map<String, Metric> ms = getMetrics("lims-test-0.1.jar");
        assertEquals(3, ms.get("DacTest1$Dac1").getDac());
    }

    @Test
    public void testNom() {
        Map<String, Metric> ms = getMetrics("lims-test-0.1.jar");
        assertEquals(3, ms.get("NomTest1$Nom1").getNom());
        assertEquals(1, ms.get("NomTest1$Nom2").getNom());
        assertEquals(1, ms.get("NomTest1$Nom3").getNom());
    }

    private Map<String, Metric> getMetrics(String name) {
        Path x = getResource(name);
        MetricCalculator mc = null;
        try {
            mc = new MetricCalculator(x);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert mc != null;
        return mc.getMetrics();
    }

    private Path getResource(String name){
        try {
            Enumeration<URL> roots = ClassLoader.getSystemClassLoader().getResources(name);
            URL url = roots.nextElement();
            return new File(url.getFile()).toPath();
        }
        catch (IOException e) {e.printStackTrace();}
        return null;
    }
}