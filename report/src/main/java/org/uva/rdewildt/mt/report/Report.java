package org.uva.rdewildt.mt.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public abstract class Report {
    private final String name;
    private final List<String> keys;
    private List<Reportable> report;

    public Report(Path filePath, Reportable reportable) throws IOException, NoSuchFieldException {
        String filename = filePath.getFileName().toString();
        this.name = filename.substring(0, filename.indexOf('.'));
        this.keys = reportable.getKeys();
        this.report = importReportsFromFile(filePath, reportable);
    }

    public Report(String name, Reportable reportable) {
        this.name = name;
        this.keys = reportable.getKeys();
        this.report = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getHeader() {
        return new ArrayList<>(this.keys);
    }

    public List<List<Object>> getBody() {
        List<List<Object>> rows = new ArrayList<>();
        this.report.forEach(reportable -> {
            List<Object> values = new ArrayList<>();
            this.keys.forEach(key -> values.add(reportable.getValues().get(key)));
            rows.add(values);
        });
        return rows;
    }

    public List<Reportable> getReport() {
        return this.report;
    }

    public void updateReport(Reportable reportable) throws Exception {
        List<String> inputKeys = reportable.getKeys();
        if (this.keys.containsAll(inputKeys)) {
            report.add(reportable);
        } else {
            throw new NoSuchFieldException("Input keys don't match report keys");
        }
    }

    public void writeToFile(Path path, String nameAddition, Character seperator, Boolean seperatorFlag) {
        Path filename = Paths.get(path.toString(), this.getName() + nameAddition + ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(filename, StandardCharsets.UTF_8)) {
            if (seperatorFlag) {
                writer.write("sep=" + seperator + "\n");
            }
            writer.write(String.join(",", this.getHeader()) + '\n');
            for (List<Object> row : this.getBody()) {
                List<Object> normalized = normalizeValues(row, seperator);
                writer.write(String.join(seperator.toString(), normalized.stream().map(Object::toString).collect(Collectors.toList())) + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Reportable> importReportsFromFile(Path path, Reportable reportType) throws IOException, NoSuchFieldException {
        List<Reportable> reportables = new ArrayList<>();

        int index = 0;
        List<String> lines = Files.readAllLines(path).stream()
                .filter(line -> !line.startsWith("//")).collect(Collectors.toList());

        Character seperator = ';';
        if (lines.get(index).contains("sep=")) {
            seperator = lines.get(index).toCharArray()[lines.get(index).length() - 1];
            index++;
        }

        List<String> keys = new ArrayList<>();
        Arrays.stream(lines.get(index).split(seperator.toString())).forEach(o -> {
            String x = o.replaceAll("\\s+", "");
            keys.add(x);
        });
        index++;

        while (index < lines.size()) {
            Map<String, Object> map = new HashMap<>();

            final int[] i = {0};
            Arrays.stream(lines.get(index).split(seperator.toString())).forEach(o -> {
                map.put(keys.get(i[0]), o);
                i[0]++;
            });

            Reportable reportable = reportType.getNewInstance();
            reportable.setValues(map);
            reportables.add(reportable);

            index++;
        }
        return reportables;
    }

    private Boolean validReport(Map<String, List<Object>> report, Reportable targetReport) {
        List<String> keys = new ArrayList<>(targetReport.getKeys());
        keys.removeAll(report.keySet());
        return keys.isEmpty();
    }

    private List<Object> normalizeValues(List<Object> values, Character sep) {
        return values.stream().map(value -> value == null ? "NIL" : value.toString().replaceAll(sep.toString(), "")).collect(Collectors.toList());
    }
}
