package ua.sirkostya009.csvstatstesttask;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    /**
     * @param totalRows the total number of rows
     * @param validRows the number of valid rows
     * @param parseTime the time spent on parsing
     * @param limit the number of top URIs to collect
     * @return A collector that collects rows into a Stats object.
     */
    public static Collector<Row, ?, Stats> collector(int totalRows, int validRows, long parseTime, int limit) {
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ").withZone(ZoneId.of("UTC"));

        return Collector.of(() -> new Stats(totalRows, validRows, parseTime),
                (stats, row) -> {
                    stats.getTopUris().merge(row.method() + '@' + row.uri(), 1L, Long::sum);
                    stats.getRequestsPerSecond().merge(
                            formatter.format(Instant.from(formatter.parse(row.date()))),
                            1L,
                            Long::sum
                    );
                },
                (stats, _s) -> stats,
                stats -> {
                    stats.setTopUris(stats.getTopUris().entrySet().stream()
                            .limit(limit)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (map, _m) -> map, TreeMap::new)));
                    return stats;
                });
    }
}
