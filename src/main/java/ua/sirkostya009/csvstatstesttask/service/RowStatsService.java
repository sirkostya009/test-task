package ua.sirkostya009.csvstatstesttask.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.sirkostya009.csvstatstesttask.mapper.Mapper;
import ua.sirkostya009.csvstatstesttask.model.Row;
import ua.sirkostya009.csvstatstesttask.model.Stats;
import ua.sirkostya009.csvstatstesttask.validator.Validator;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RowStatsService implements ParseService<Row, Stats> {
    @Getter
    private final CSVFormat format = CSVFormat.newFormat(';');
    @Getter
    private final Validator<Row> validator;
    @Getter
    private final Mapper<Row> mapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ").withZone(ZoneId.of("UTC"));

    @Override
    public Stats apply(MultipartFile[] files, Map<String, Object> context) {
        var start = System.currentTimeMillis();
        var limit = (int) context.getOrDefault("limit", 5);

        var rows = parseRows(files);
        var valid = filterValidRows(rows);

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
}
