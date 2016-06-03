package org.uva.rdewildt.mt.report;

import org.uva.rdewildt.mt.utils.MapUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.uva.rdewildt.mt.utils.MapUtils.addValueToMapList;
import static org.uva.rdewildt.mt.utils.MapUtils.transposeValues;

public abstract class Report {
    private String name;
    private Map<String, List<Object>> report;

    public Report(Path filePath, Reportable reportable) throws IOException {
        String filename = filePath.getFileName().toString();
        this.name = filename.substring(0, filename.indexOf('.'));
        Map<String, List<Object>> temp = this.report = readFromFile(filePath);
        if(validReport(report, reportable)){
            this.report = temp;
        }
        else {
            throw new IOException();
        }
    }

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

    public Map<String, List<Object>> getReport() {
        return report;
    }

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

    public void writeToFile(Path path, String nameAddition, Character seperator, Boolean seperatorFlag) throws IOException {
        String filename = Paths.get(path.toString(), this.getName() + nameAddition + ".csv").toString();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8));
        if(seperatorFlag){
            writer.write("sep=" + seperator + "\n");
        }
        writer.write(String.join(",", this.getHeader()) + '\n');
        for(List<Object> row : this.getBody()){
            List<Object> normalized = normalizeValues(row, seperator);
            writer.write(String.join(seperator.toString(), normalized.stream().map(Object::toString).collect(Collectors.toList())) + '\n');
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

    private Map<String, List<Object>> readFromFile(Path path) throws IOException {
        Map<String, List<Object>> map = new LinkedHashMap<>();

        int index = 0;
        List<String> lines = Files.readAllLines(path);

        Character seperator = ',';
        if(lines.get(index).contains("sep=")){
            seperator = lines.get(index).toCharArray()[lines.get(index).length() - 1];
            index++;
        }

        List<String> indices = new ArrayList<>();
        Arrays.stream(lines.get(index).split(seperator.toString())).forEach(o -> {
            String x = o.replaceAll("\\s+", "");
            map.put(x, null);
            indices.add(x);
        });
        index++;

        while(index < lines.size()) {
            final int[] i = {0};
            Arrays.stream(lines.get(index).split(seperator.toString())).forEach(o -> {
                MapUtils.addValueToMapList(map, indices.get(i[0]), o);
                i[0]++;
            });
            index++;
        }
        return map;
    }

    private Boolean validReport(Map<String, List<Object>> report, Reportable targetReport){
        List<String> keys = new ArrayList<>(targetReport.getKeys());
        keys.removeAll(report.keySet());
        return keys.isEmpty();
    }

    private List<Object> normalizeValues(List<Object> values, Character sep){
        return values.stream().map(value -> value == null ? "NIL" : value.toString().replaceAll(sep.toString(), "")).collect(Collectors.toList());
    }
}
