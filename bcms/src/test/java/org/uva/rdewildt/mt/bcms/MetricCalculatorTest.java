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
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(16, ms.get("WmcTest1").getWmc());
    }

    @Test
    public void testNoc() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(3, ms.get("NocTest1$Super").getNoc());
        assertEquals(2, ms.get("NocTest1$Child1").getNoc());
        assertEquals(0, ms.get("NocTest1$Child2").getNoc());
        assertEquals(0, ms.get("NocTest1$Child3").getNoc());
        assertEquals(0, ms.get("NocTest1$Child11").getNoc());
        assertEquals(0, ms.get("NocTest1$Child12").getNoc());
    }

    @Test
    public void testRfc() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(7, ms.get("RfcTest1$Main").getRfc());
        assertEquals(3, ms.get("RfcTest1$ExternClass1").getRfc());
    }

    @Test
    public void testCbo(){
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(9, ms.get("CboTest1$Main1").getCbo());
        assertEquals(5, ms.get("CboTest1$Main2").getCbo());
        assertEquals(4, ms.get("CboTest1$Couple1").getCbo());
        assertEquals(3, ms.get("CboTest1$Couple2").getCbo());
        assertEquals(4, ms.get("CboTest1$Couple3").getCbo());
        assertEquals(2, ms.get("CboTest1$InnerCouple1").getCbo());
    }

    @Test
    public void testDit() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(1, ms.get("DitTest1$Super").getDit());
        assertEquals(2, ms.get("DitTest1$Child").getDit());
        assertEquals(3, ms.get("DitTest1$Infant").getDit());
        assertEquals(1, ms.get("DitTest1$Interface").getDit());
        assertEquals(1, ms.get("DitTest1$Implementation").getDit());
        assertEquals(4, ms.get("DitTest1$NewList").getDit());

    }

    @Test
    public void testLcom() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(6, ms.get("LcomTest1$Lcom1").getLcom());
        assertEquals(0, ms.get("LcomTest1$Lcom2").getLcom());
        assertEquals(0, ms.get("LcomTest1$Lcom3").getLcom());
        assertEquals(0, ms.get("LcomTest1$Lcom4").getLcom());
    }

    @Test
    public void testMpc() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(7, ms.get("MpcTest1$Mpc1").getMpc());
        assertEquals(1, ms.get("MpcTest1$Mpc2").getMpc());
        assertEquals(1, ms.get("MpcTest1$Mpc3").getMpc());

    }

    @Test
    public void testDac() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(3, ms.get("DacTest1$Dac1").getDac());
    }

    @Test
    public void testNom() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(3, ms.get("NomTest1$Nom1").getNom());
        assertEquals(1, ms.get("NomTest1$Nom2").getNom());
        assertEquals(1, ms.get("NomTest1$Nom3").getNom());
    }

    @Test
    public void testAcaic() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx1").getAcaic());
        assertEquals(1, ms.get("XcaxxTest1$Xcaxx2").getAcaic());
        assertEquals(1, ms.get("XcaxxTest1$Xcaxx3").getAcaic());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx4").getAcaic());
    }

    @Test
    public void testAcaec() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(2, ms.get("XcaxxTest1$Xcaxx1").getAcaec());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx2").getAcaec());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx3").getAcaec());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx4").getAcaec());
    }

    @Test
    public void testDcaic() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(2, ms.get("XcaxxTest1$Xcaxx1").getDcaic());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx2").getDcaic());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx3").getDcaic());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx4").getDcaic());
    }

    @Test
    public void testDcaec() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx1").getDcaec());
        assertEquals(1, ms.get("XcaxxTest1$Xcaxx2").getDcaec());
        assertEquals(1, ms.get("XcaxxTest1$Xcaxx3").getDcaec());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx4").getDcaec());
    }

    @Test
    public void testOcaic() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(2, ms.get("XcaxxTest1$Xcaxx1").getOcaic());
        assertEquals(1, ms.get("XcaxxTest1$Xcaxx2").getOcaic());
        assertEquals(1, ms.get("XcaxxTest1$Xcaxx3").getOcaic());
        assertEquals(2, ms.get("XcaxxTest1$Xcaxx4").getOcaic());
    }

    @Test
    public void testOcaec() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(1, ms.get("XcaxxTest1$Xcaxx1").getOcaec());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx2").getOcaec());
        assertEquals(0, ms.get("XcaxxTest1$Xcaxx3").getOcaec());
        assertEquals(1, ms.get("XcaxxTest1$Xcaxx4").getOcaec());
    }

    @Test
    public void testAcmic() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx1").getAcmic());
        assertEquals(1, ms.get("XcmxxTest1$Xcmxx2").getAcmic());
        assertEquals(2, ms.get("XcmxxTest1$Xcmxx3").getAcmic());
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx4").getAcmic());
    }

    @Test
    public void testAcmec() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(2, ms.get("XcmxxTest1$Xcmxx1").getAcmec());
        assertEquals(1, ms.get("XcmxxTest1$Xcmxx2").getAcmec());
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx3").getAcmec());
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx4").getAcmec());
    }

    @Test
    public void testDcmic() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(2, ms.get("XcmxxTest1$Xcmxx1").getDcmic());
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx2").getDcmic());
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx3").getDcmic());
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx4").getDcmic());
    }

    @Test
    public void testDcmec() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx1").getDcmec());
        assertEquals(1, ms.get("XcmxxTest1$Xcmxx2").getDcmec());
        assertEquals(1, ms.get("XcmxxTest1$Xcmxx3").getDcmec());
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx4").getDcmec());
    }

    @Test
    public void testOcmic() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(2, ms.get("XcmxxTest1$Xcmxx1").getOcmic());
        assertEquals(1, ms.get("XcmxxTest1$Xcmxx2").getOcmic());
        assertEquals(1, ms.get("XcmxxTest1$Xcmxx3").getOcmic());
        assertEquals(2, ms.get("XcmxxTest1$Xcmxx4").getOcmic());
    }

    @Test
    public void testOcmec() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(1, ms.get("XcmxxTest1$Xcmxx1").getOcmec());
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx2").getOcmec());
        assertEquals(0, ms.get("XcmxxTest1$Xcmxx3").getOcmec());
        assertEquals(1, ms.get("XcmxxTest1$Xcmxx4").getOcmec());
    }

    @Test
    public void testOvo() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(3, ms.get("PolyTest1$Poly1").getOvo());
        assertEquals(0, ms.get("PolyTest1$Poly2").getOvo());
        assertEquals(0, ms.get("PolyTest1$Poly3").getOvo());
        assertEquals(0, ms.get("PolyTest1$Poly4").getOvo());
    }

    @Test
    public void testSp() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(2, ms.get("PolyTest1$Poly1").getSp());
        assertEquals(1, ms.get("PolyTest1$Poly2").getSp());
        assertEquals(1, ms.get("PolyTest1$Poly3").getSp());
        assertEquals(0, ms.get("PolyTest1$Poly4").getSp());
    }

    @Test
    public void testSpa() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(2, ms.get("PolyTest1$Poly1").getSpa());
        assertEquals(0, ms.get("PolyTest1$Poly2").getSpa());
        assertEquals(0, ms.get("PolyTest1$Poly3").getSpa());
        assertEquals(0, ms.get("PolyTest1$Poly4").getSpa());
    }

    @Test
    public void testSpd() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(0, ms.get("PolyTest1$Poly1").getSpd());
        assertEquals(1, ms.get("PolyTest1$Poly2").getSpd());
        assertEquals(1, ms.get("PolyTest1$Poly3").getSpd());
        assertEquals(0, ms.get("PolyTest1$Poly4").getSpd());
    }

    @Test
    public void testDp() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(1, ms.get("PolyTest1$Poly1").getDp());
        assertEquals(1, ms.get("PolyTest1$Poly2").getDp());
        assertEquals(2, ms.get("PolyTest1$Poly3").getDp());
        assertEquals(0, ms.get("PolyTest1$Poly4").getDp());
    }

    @Test
    public void testDpa() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(1, ms.get("PolyTest1$Poly1").getDpa());
        assertEquals(1, ms.get("PolyTest1$Poly2").getDpa());
        assertEquals(0, ms.get("PolyTest1$Poly3").getDpa());
        assertEquals(0, ms.get("PolyTest1$Poly4").getDpa());
    }

    @Test
    public void testDpd() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(0, ms.get("PolyTest1$Poly1").getDpd());
        assertEquals(0, ms.get("PolyTest1$Poly2").getDpd());
        assertEquals(2, ms.get("PolyTest1$Poly3").getDpd());
        assertEquals(0, ms.get("PolyTest1$Poly4").getDpd());
    }

    @Test
    public void testNip() {
        Map<String, Metric> ms = getMetrics("bcmstest.jar");
        assertEquals(1, ms.get("PolyTest1$Poly1").getNip());
        assertEquals(1, ms.get("PolyTest1$Poly2").getNip());
        assertEquals(1, ms.get("PolyTest1$Poly3").getNip());
        assertEquals(3, ms.get("PolyTest1$Poly4").getNip());
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
