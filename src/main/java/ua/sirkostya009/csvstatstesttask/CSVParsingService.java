package ua.sirkostya009.csvstatstesttask;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CSVParsingService {
    private final static int IP = 0;
    private final static int DATE = 1;
    private final static int METHOD = 2;
    private final static int URI = 3;
    private final static int STATUS = 4;

    public Stats parse(MultipartFile[] files, int limit) {
        var start = Instant.now().getNano();

        var records = Arrays.stream(files)
                .flatMap(file -> {
                    try(var parser = openFile(file)) {
                        return parser.getRecords().stream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        var valid = records.stream().filter(this::validate).toList();

        return Stats.builder()
                .topUris(valid.stream()
                        .collect(Collectors.groupingBy(record -> record.get(URI),
                                                       Collectors.counting()))
                        .entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(limit)
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                                  Map.Entry::getValue)))
                .requestsPerSecond(valid.stream()
                        .collect(Collector.of(() -> new TreeMap<>(Comparator.reverseOrder()),
                                              (map, record) -> map.merge(record.get(DATE), 1L, Long::sum),
                                              (map, _m) -> map)))
                .totalRows(records.size())
                .validRows(valid.size())
                .parseTime(Duration.ofNanos(Instant.now().getNano() - start).toNanos())
                .build();
    }

    private boolean validate(CSVRecord record) {
        try {
            var bytes = Arrays.stream(record.get(IP).split("\\.")).mapToInt(Integer::parseInt).toArray();
            int status = record.get(STATUS).charAt(0) - '0';

            return Pattern.matches("^\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}$", record.get(IP))
                    && Pattern.matches("^\\d{2}/\\d{2}/\\d{4}:\\d{2}:\\d{2}:\\d{2}-\\d{4}$", record.get(DATE))
                    && Pattern.matches("^GET|POST|PUT|DELETE|HEAD|OPTIONS|TRACE|CONNECT$", record.get(METHOD))
                    && Pattern.matches("^/.*+$", record.get(URI))
                    && Pattern.matches("^\\d{3}$", record.get(STATUS))
                    && bytes[0] != 0 && Arrays.stream(bytes).allMatch(b -> b < 256)
                    && status >= 1 && status <= 5;
        } catch (Exception ignored) {
            return false;
        }
    }

    private CSVParser openFile(MultipartFile file) throws IOException {
        return CSVFormat.newFormat(';').parse(new InputStreamReader(file.getInputStream()));
    }
}
