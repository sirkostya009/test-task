package ua.sirkostya009.csvstatstesttask;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final Validator validator;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ").withZone(ZoneId.of("UTC"));

    @Operation(
            summary = "Parse CSV files and return statistics",
            description = "Only returns an error if something went wrong internally, i.e., not client's fault",
            parameters = @Parameter(name = "limit", description = "The number of top URIs to collect"),
            requestBody = @RequestBody(
                    description = "CSV files to parse. Note that header is not required",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schemaProperties = @SchemaProperty(
                                    name = "files",
                                    array = @ArraySchema(items = @Schema(
                                            format = "binary",
                                            type = "string"
                                    ))
                            ),
                            examples = @ExampleObject(
                                    summary = "example csv file contents",
                                    value = """
                                            192.168.2.212;28/07/2006:10:27:10-0300;GET;/user/try/;200
                                            192.168.2.212;28/07/2006:10:22:04-0300;GET;/;200
                                            192.168.2.220;28/07/2006:10:25:04-0300;PUT;/save/;200
                                            192.168.2.111;28/07/2006:10:25:04-0300;PUT;/save/;403"""
                            ),
                            encoding = @Encoding(name = "files", contentType = "text/csv")
                    )
            ),
            tags = {"CSV"}
    )
    @PostMapping(path = "/parse", consumes = "multipart/form-data", produces = "application/json")
    public Stats upload(@RequestPart("files") MultipartFile[] files,
                        @RequestParam(defaultValue = "5") int limit) {
        var start = System.currentTimeMillis();

        var rows = Arrays.stream(files)
                .flatMap(csvRecordStream(CSVFormat.newFormat(';')))
                .map(Row::of)
                .toList();

        var valid = rows.stream().filter(Validator.normalize(validator::validateRow)).toList();

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
}
