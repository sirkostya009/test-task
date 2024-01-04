package ua.sirkostya009.csvstatstesttask;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
@AllArgsConstructor
public class Stats {
    private Map<String, Long> topUris;
    private Map<String, Long> requestsPerSecond;
    private int totalRows;
    private int validRows;
    private long parseTime;

    public Stats(int totalRows, int validRows, long parseTime) {
        this(new TreeMap<>(), new TreeMap<>(Comparator.reverseOrder()), totalRows, validRows, parseTime);
    }
}
