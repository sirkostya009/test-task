package ua.sirkostya009.csvstatstesttask.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.sirkostya009.csvstatstesttask.model.Row;
import ua.sirkostya009.csvstatstesttask.model.Stats;
import ua.sirkostya009.csvstatstesttask.validator.RowValidator;

import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ParseService {
    private final RowValidator validator;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ").withZone(ZoneId.of("UTC"));

    public Stats parse(MultipartFile[] files, int limit) {
        var start = System.currentTimeMillis();

        var rows = Arrays.stream(files)
                .flatMap(csvRecordStream(CSVFormat.newFormat(';')))
                .map(this::toRow)
                .toList();

        var valid = rows.stream().filter(validator::validate).toList();

        return Stats.builder()
                .topUris(valid.stream()
                        .collect(Collectors.groupingBy(row -> row.method() + '@' + row.uri(), Collectors.counting()))
                        .entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(limit)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (m1, m2) -> m1,
                                () -> new LinkedHashMap<>(limit)
                        )))
                .requestsPerSecond(valid.stream()
                        .collect(Collectors.groupingBy(row -> formatter.format(Instant.from(formatter.parse(row.date()))),
                                Collectors.counting()))
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (m1, m2) -> m1,
                                () -> new TreeMap<String, Long>(Comparator.reverseOrder())
                        )))
                .totalRows(rows.size())
                .validRows(valid.size())
                .parseTime(System.currentTimeMillis() - start)
                .build();
    }

    private Function<MultipartFile, Stream<CSVRecord>> csvRecordStream(CSVFormat format) {
        return file -> {
            try(var parser = format.parse(new InputStreamReader(file.getInputStream()))) {
                return parser.getRecords().stream();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Row toRow(CSVRecord record) {
        return new Row(
                record.get(0),
                record.get(1),
                record.get(2),
                record.get(3),
                record.get(4)
        );
    }
}
