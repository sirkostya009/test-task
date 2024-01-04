package ua.sirkostya009.csvstatstesttask;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Stats {
    private Map<String, Long> topUris;
    private Map<String, Long> requestsPerSecond;
    private int totalRows;
    private int validRows;
    private long parseTime;

    public Stats(int totalRows, int validRows, long parseTime) {
        this(new TreeMap<>(Comparator.reverseOrder()), new TreeMap<>(Comparator.reverseOrder()), totalRows, validRows, parseTime);
    }

    /**
     * @param totalRows the total number of rows
     * @param validRows the number of valid rows
     * @param parseTime the time spent on parsing
     * @param limit the number of top URIs to collect
     * @return A collector that collects rows into a Stats object.
     */
    public static Collector<Row, ?, Stats> collector(int totalRows, int validRows, long parseTime, int limit) {
        return Collector.of(() -> new Stats(totalRows, validRows, parseTime),
                            (stats, row) -> {
                                stats.topUris.merge(row.uri(), 1L, Long::sum);
                                stats.requestsPerSecond.merge(row.date(), 1L, Long::sum);
                            },
                            (stats, _s) -> stats,
                            stats -> {
                                stats.topUris = stats.topUris.entrySet().stream()
                                        .limit(limit)
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                                return stats;
                            });
    }
}
