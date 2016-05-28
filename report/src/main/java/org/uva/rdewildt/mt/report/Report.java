package org.uva.rdewildt.mt.report;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Report {
    private final String name;
    private final Map<String, List<Object>> report;

    public Report(String name, Reportable reportable) {
        this.name = name;
        this.report = initReport(reportable);
    }

    public String getName() {
        return name;
    }

    public List<String> getHeader(){
        return new ArrayList<>(report.keySet());
    }

    public List<List<Object>> getBody(){ return transposeValues(this.report); }

    public void updateReport(Map<String, Object> row) throws NoSuchFieldException {
        if(getHeader().containsAll(row.keySet())){
            for(Map.Entry<String, Object> entry : row.entrySet()){
                addValueToMapList(this.report, entry.getKey(), entry.getValue());
            }
        }
        else{
            throw new NoSuchFieldException();
        }
    }

    public void writeToFile(String nameAddition, Character seperator, Boolean seperatorFlag) throws IOException {
        FileWriter writer = new FileWriter(this.getName() + nameAddition + ".csv");
        if(seperatorFlag){
            writer.write("sep=" + seperator + "\n");
        }
        writer.write(String.join(",", this.getHeader()) + '\n');
        for(List<Object> row : this.getBody()){
            Collections.replaceAll(row, null, "NIL");
            writer.write(String.join(seperator.toString(), row.stream().map(Object::toString).collect(Collectors.toList())) + '\n');
        }
        writer.close();
    }

    private Map<String, List<Object>> initReport(Reportable reportable){
        Map<String, List<Object>> emptyReport = new LinkedHashMap<>();
        for(String column : reportable.getKeys()){
            emptyReport.put(column, null);
        }
        return emptyReport;
    }

    private <K,V> List<List<V>> transposeValues(Map<K, List<V>> map){
        if(map.size() <= 0) {
            return new ArrayList<>();
        }
        List<List<V>> values = new ArrayList<>(map.values());
        List<List<V>> newvalues = new ArrayList<>();
        if (values.get(0) != null) {
            for(int i = 0; i < values.get(0).size(); i++){
                List<V> row = new ArrayList<>();
                for(int j = 0; j < values.size(); j++){
                    row.add(values.get(j).get(i));
                }
                newvalues.add(row);
            }
        }
        return newvalues;
    }

    private <K, V> void addValueToMapList(Map<K, List<V>> map, K key, V value) {
        if (map.get(key) == null) {
            map.put(key, new ArrayList<V>() {{ add(value); }});
        }
        else {
            List<V> newvalue = map.get(key);
            newvalue.add(value);
            map.put(key, newvalue);
        }
    }


}
