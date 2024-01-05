package ua.sirkostya009.csvstatstesttask;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final Validator validator;

    @Operation(
            summary = "Parse CSV files and return statistics",
            parameters = {
                    @Parameter(
                            name = "files",
                            description = "CSV files to parse. Note that header is not required",
                            required = true,
                            array = @ArraySchema(minItems = 1)
                    ),
                    @Parameter(name = "limit", description = "The number of top URIs to collect")
            },
            requestBody = @RequestBody(
                    description = "CSV files to parse. Note that header is not required",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = MultipartFile[].class, accessMode = Schema.AccessMode.READ_ONLY),
                            encoding = @Encoding(name = "files", contentType = "text/csv")
                    )
            ),
            tags = {"CSV"}
    )
    @PostMapping(path = "/parse", consumes = "multipart/form-data", produces = "application/json")
    public Stats upload(@RequestPart("files") MultipartFile[] files,
                        @RequestParam(defaultValue = "5") int limit) {
        var start = System.nanoTime();

        var rows = Arrays.stream(files).collect(
                csvParsingCollector(CSVFormat.newFormat(';'), Row::of)
        );

        var valid = rows.stream().filter(Validator.normalize(validator::validateRow)).toList();

        return valid.stream().collect(
                Stats.collector(rows.size(), valid.size(), System.nanoTime() - start, limit)
        );
    }

    /**
     * @param format the CSV file format
     * @param mapper a function that maps CSVRecord to T
     * @return A collector that parses CSV files into a list of objects.
     */
    private <T> Collector<MultipartFile, ?, List<T>> csvParsingCollector(CSVFormat format, Function<CSVRecord, T> mapper) {
        return Collectors.flatMapping(multiFile -> {
            try (var parser = format.parse(new InputStreamReader(multiFile.getInputStream()))) {
                return parser.getRecords().stream().map(mapper);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Collectors.toList());
    }
}
