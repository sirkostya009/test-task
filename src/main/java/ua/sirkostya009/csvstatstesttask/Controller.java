package ua.sirkostya009.csvstatstesttask;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final Mapper mapper;
    private final Validator validator;

    @Operation(summary = "Parse CSV files and return statistics")
    @PostMapping("/parse")
    public Stats upload(@RequestPart("files") MultipartFile[] files,
                        @RequestParam(defaultValue = "5") int limit) {
        var start = System.nanoTime();

        var rows = Arrays.stream(files).collect(
                CustomCollectors.csvParsingCollector(CSVFormat.newFormat(';'), mapper::toRow)
        );

        var valid = rows.stream().filter(Validator.normalize(validator::validateRow)).toList();

        return valid.stream().collect(
                CustomCollectors.statsCollector(rows.size(), valid.size(), System.nanoTime() - start, limit)
        );
    }
}
