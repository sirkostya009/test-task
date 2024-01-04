package ua.sirkostya009.csvstatstesttask;

import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@UtilityClass
public class CustomCollectors {
    /**
     * @param totalRows the total number of rows
     * @param validRows the number of valid rows
     * @param parseTime the time spent on parsing
     * @param limit the number of top URIs to collect
     * @return A collector that collects rows into a Stats object.
     */
    public Collector<Row, ?, Stats> statsCollector(int totalRows, int validRows, long parseTime, int limit) {
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ").withZone(ZoneId.of("UTC"));

        return Collector.of(() -> new Stats(totalRows, validRows, parseTime),
                            (stats, row) -> {
                                stats.getTopUris().merge(row.uri(), 1L, Long::sum);
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

    /**
     * A collector that parses CSV files into a list of objects.
     * @param format the CSV format
     * @param mapper a function that maps a CSVRecord instance to an object
     * @return A collector that parses CSV files into a list of objects.
     */
    public <T> Collector<MultipartFile, ?, List<T>> csvParsingCollector(CSVFormat format, Function<CSVRecord, T> mapper) {
        return Collectors.flatMapping(multiFile -> {
            try (var parser = format.parse(new InputStreamReader(multiFile.getInputStream()))) {
                return parser.getRecords().stream().map(mapper);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Collectors.toList());
    }
}
