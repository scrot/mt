package org.uva.rdewildt.mt.bcms;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by roy on 5/21/16.
 */
public class MetricCalculatorTest {

    @Test
    public void testWmc(){
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(16, ms.get("WmcTest1").getWmc());
    }

    @Test
    public void testNoc() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(3, ms.get("NocTest1$Super").getNoc());
        assertEquals(2, ms.get("NocTest1$Child1").getNoc());
        assertEquals(0, ms.get("NocTest1$Child2").getNoc());
        assertEquals(0, ms.get("NocTest1$Child3").getNoc());
        assertEquals(0, ms.get("NocTest1$Child11").getNoc());
        assertEquals(0, ms.get("NocTest1$Child12").getNoc());
    }

    @Test
    public void testRfc() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(7, ms.get("RfcTest1$Main").getRfc());
        assertEquals(3, ms.get("RfcTest1$ExternClass1").getRfc());
    }

    @Test
    public void testCbo(){
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(9, ms.get("CboTest1$Main1").getCbo());
        assertEquals(5, ms.get("CboTest1$Main2").getCbo());
        assertEquals(4, ms.get("CboTest1$Couple1").getCbo());
        assertEquals(3, ms.get("CboTest1$Couple2").getCbo());
        assertEquals(4, ms.get("CboTest1$Couple3").getCbo());
        assertEquals(2, ms.get("CboTest1$InnerCouple1").getCbo());
    }

    @Test
    public void testDit() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(1, ms.get("DitTest1$Super").getDit());
        assertEquals(2, ms.get("DitTest1$Child").getDit());
        assertEquals(3, ms.get("DitTest1$Infant").getDit());
        assertEquals(1, ms.get("DitTest1$Interface").getDit());
        assertEquals(1, ms.get("DitTest1$Implementation").getDit());
        assertEquals(4, ms.get("DitTest1$NewList").getDit());

    }

    @Test
    public void testLcom() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(6, ms.get("LcomTest1$Lcom1").getLcom());
        assertEquals(0, ms.get("LcomTest1$Lcom2").getLcom());
        assertEquals(0, ms.get("LcomTest1$Lcom3").getLcom());
        assertEquals(0, ms.get("LcomTest1$Lcom4").getLcom());
    }

    @Test
    public void testMpc() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(7, ms.get("MpcTest1$Mpc1").getMpc());
        assertEquals(1, ms.get("MpcTest1$Mpc2").getMpc());
        assertEquals(1, ms.get("MpcTest1$Mpc3").getMpc());

    }

    @Test
    public void testDac() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(3, ms.get("DacTest1$Dac1").getDac());
    }

    @Test
    public void testNom() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(3, ms.get("NomTest1$Nom1").getNom());
        assertEquals(1, ms.get("NomTest1$Nom2").getNom());
        assertEquals(1, ms.get("NomTest1$Nom3").getNom());
    }

    @Test
    public void testAcmic() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(0, ms.get("AcmicTest1$Acmic1").getAcmic());
        assertEquals(0, ms.get("AcmicTest1$Acmic2").getAcmic());
        assertEquals(0, ms.get("AcmicTest1$Acmic3").getAcmic());
        assertEquals(1, ms.get("AcmicTest1$Acmic4").getAcmic());
        assertEquals(2, ms.get("AcmicTest1$Acmic5").getAcmic());
    }

    @Test
    public void testAcmec() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(0, ms.get("AcmecTest1$Acmec1").getAcmec());
        assertEquals(1, ms.get("AcmecTest1$Acmec2").getAcmec());
        assertEquals(1, ms.get("AcmecTest1$Acmec3").getAcmec());
        assertEquals(0, ms.get("AcmecTest1$Acmec4").getAcmec());
        assertEquals(0, ms.get("AcmecTest1$Acmec5").getAcmec());
    }

    @Test
    public void testDcmic() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(2, ms.get("DcmicTest1$Dcmic1").getDcmic());
        assertEquals(0, ms.get("DcmicTest1$Dcmic2").getDcmic());
        assertEquals(0, ms.get("DcmicTest1$Dcmic3").getDcmic());
        assertEquals(0, ms.get("DcmicTest1$Dcmic4").getDcmic());
        assertEquals(0, ms.get("DcmicTest1$Dcmic5").getDcmic());
    }

    @Test
    public void testDcmec() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(3, ms.get("DcmecTest1$Dcmec1").getDcmec());
        assertEquals(0, ms.get("DcmecTest1$Dcmec2").getDcmec());
        assertEquals(0, ms.get("DcmecTest1$Dcmec3").getDcmec());
        assertEquals(0, ms.get("DcmecTest1$Dcmec4").getDcmec());
        assertEquals(0, ms.get("DcmecTest1$Dcmec5").getDcmec());
    }

    @Test
    public void testOcmic() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(5, ms.get("OcmicTest1$Ocmic1").getOcmic());
        assertEquals(2, ms.get("OcmicTest1$Ocmic2").getOcmic());
        assertEquals(1, ms.get("OcmicTest1$Ocmic3").getOcmic());
        assertEquals(2, ms.get("OcmicTest1$Ocmic4").getOcmic());
        assertEquals(1, ms.get("OcmicTest1$Ocmic5").getOcmic());
    }

    @Test
    public void testOcmec() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(2, ms.get("OcmecTest1$Ocmec1").getOcmec());
        assertEquals(0, ms.get("OcmecTest1$Ocmec2").getOcmec());
        assertEquals(1, ms.get("OcmecTest1$Ocmec3").getOcmec());
        assertEquals(0, ms.get("OcmecTest1$Ocmec4").getOcmec());
        assertEquals(0, ms.get("OcmecTest1$Ocmec5").getOcmec());
    }

    @Test
    public void testNip() {
        Map<String, Metric> ms = getMetrics("limstest.jar");
        assertEquals(3, ms.get("NipTest1$Nip4").getNip());
    }

    private Map<String, Metric> getMetrics(String name) {
        Path x = getResource(name);
        return new MetricCalculator(x, false).getMetrics();
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
