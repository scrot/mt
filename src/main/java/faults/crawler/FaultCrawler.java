package faults.crawler;

import faults.Fault;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface FaultCrawler {
    Map<Path, List<Fault>> getClassFaults();
}
