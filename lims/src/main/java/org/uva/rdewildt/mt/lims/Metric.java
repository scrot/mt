package org.uva.rdewildt.mt.lims;

import org.uva.rdewildt.mt.report.Reportable;
import org.uva.rdewildt.mt.utils.MapUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/22/16.
 */
public class Metric implements Reportable {
    protected final Map<String, Object> map = new LinkedHashMap<>();

    public Metric(){
        this("");
    }

    public Metric(String classname){
        this(classname,0,0,0,0,0,0,0,0,0,0,0);

    }

    public Metric(Metric metric){
        this(metric.getClassname(), metric.getWmc(), metric.getNoc(),
        metric.getRfc(), metric.getCbo(), metric.getDit(),
        metric.getLcom(), metric.getMpc(), metric.getDac(),
        metric.getNom(), metric.getSize1(), metric.getSize2());

    }

    public Metric(String classname, int wmc, int noc, int rfc, int cbo, int dit,
                  int lcom, int mpc, int dac, int nom, int size1,
                  int size2) {
        this.map.putAll(new LinkedHashMap<String, Object>(){{
            put("Class", classname);
            put("WMC", wmc);
            put("DIT", noc);
            put("NOC", rfc);
            put("CBO", cbo);
            put("RFC", dit);
            put("LCOM", lcom);
            put("DAC", mpc);
            put("MPC", dac);
            put("NOM", nom);
            put("SIZE1", size1);
            put("SIZE2", size2);
        }});
    }
    @Override
    public List<String> getKeys(){
        return new ArrayList<>(this.map.keySet());
    }

    @Override
    public Map<String, Object> getValues(){
        return this.map;
    }

    public String getClassname() {
        return (String) map.get("Class");
    }

    public int getWmc() {
        return (Integer) map.get("WMC");
    }

    public int getNoc() {
        return (Integer) map.get("NOC");
    }

    public int getRfc() { return (Integer) map.get("RFC"); }

    public int getCbo() {
        return (Integer) map.get("CBO");
    }

    public int getDit() {
        return (Integer) map.get("DIT");
    }

    public int getLcom() {
        return (Integer) map.get("LCOM");
    }

    public int getMpc() {
        return (Integer) map.get("MPC");
    }

    public int getDac() {
        return (Integer) map.get("DAC");
    }

    public int getNom() {
        return (Integer) map.get("NOM");
    }

    public int getSize1() { return (Integer) map.get("SIZE1"); }

    public int getSize2() { return (Integer) map.get("SIZE2"); }

    public void incrementWmc(Integer increment){
        MapUtils.incrementMapValue(map, "WMC", increment);
    }

    public void incrementRfc(Integer increment){
        MapUtils.incrementMapValue(map, "RFC", increment);
    }

    public void incrementNoc(Integer increment){
        MapUtils.incrementMapValue(map, "NOC", increment);
    }

    public void incrementDit(Integer increment){
        MapUtils.incrementMapValue(map, "DIT", increment);
    }

    public void incrementCbo(Integer increment){
        MapUtils.incrementMapValue(map, "CBO", increment);
    }

    public void incrementLcom(Integer increment){
        MapUtils.incrementMapValue(map, "LCOM", increment);
    }

    public void incrementMpc(Integer increment){
        MapUtils.incrementMapValue(map, "MPC", increment);
    }

    public void incrementDac(Integer increment){
        MapUtils.incrementMapValue(map, "DAC", increment);
    }

    public void incrementNom(Integer increment){
        MapUtils.incrementMapValue(map, "NOM", increment);
    }

    public void incrementSize1(Integer increment){ MapUtils.incrementMapValue(map, "SIZE1", increment); }

    public void incrementSize2(Integer increment){ MapUtils.incrementMapValue(map, "SIZE2", increment); }
}
