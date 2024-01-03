package ua.sirkostya009.csvstatstesttask;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class CSVParsingService {
    private final static int DATE = 1;
    private final static int URI = 3;

    // 28/07/2006:10:27:10-0300
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ");

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
                .requestsPerSecond(new TreeMap<>(valid.stream()
                        .map(record -> Map.entry(LocalDateTime.parse(record.get(DATE), formatter), record.get(DATE)))
                        .sorted(Map.Entry.comparingByKey())
                        .collect(Collectors.groupingBy(Map.Entry::getValue,
                                                       Collectors.counting()))))
                .totalRows(records.size())
                .validRows(valid.size())
                .parseTime(Instant.now().minusNanos(start))
                .build();
    }

    private boolean validate(CSVRecord record) {
        return true;
    }

    private CSVParser openFile(MultipartFile file) throws IOException {
        return CSVFormat.newFormat(';').parse(new InputStreamReader(file.getInputStream()));
    }
}
