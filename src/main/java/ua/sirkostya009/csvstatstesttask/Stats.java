package ua.sirkostya009.csvstatstesttask;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public record Stats(
        Map<String, Long> topUris,
        Map<String, Long> requestsPerSecond,
        int totalRows,
        int validRows,
        long parseTime
) {
    private Stats(int totalRows, int validRows, long parseTime) {
        this(new TreeMap<>(), new TreeMap<>(Comparator.reverseOrder()), totalRows, validRows, parseTime);
    }

    /**
     * @param totalRows total number of rows
     * @param validRows number of valid rows
     * @param parseTime time spent on parsing
     * @param limit     number of top URIs to collect
     * @return A collector that collects rows into a Stats object.
     */
    public static Collector<Row, ?, Stats> collector(int totalRows, int validRows, long parseTime, int limit) {
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ").withZone(ZoneId.of("UTC"));

        return Collector.of(() -> new Stats(totalRows, validRows, parseTime),
                            (stats, row) -> {
                                stats.topUris().merge(row.method() + '@' + row.uri(), 1L, Long::sum);
                                stats.requestsPerSecond().merge(
                                        formatter.format(Instant.from(formatter.parse(row.date()))),
                                        1L,
                                        Long::sum
                                );
                            },
                            (stats, _s) -> stats,
                            stats -> {
                                var top = stats.topUris().entrySet().stream()
                                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                        .limit(limit)
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                                stats.topUris.clear();
                                stats.topUris.putAll(top);
                                return stats;
                            });
    }
}
