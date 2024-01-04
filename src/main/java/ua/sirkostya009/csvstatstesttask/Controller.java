package ua.sirkostya009.csvstatstesttask;

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
    private final Mapper mapper;
    private final Validator validator;

    @PostMapping("/parse")
    public Stats upload(@RequestPart("files") MultipartFile[] files,
                        @RequestParam(defaultValue = "5") int limit) {
        var start = System.nanoTime();

        var rows = Arrays.stream(files).collect(listCollector(CSVFormat.newFormat(';'), mapper::toRow));

        var valid = rows.stream().filter(Validator.normalize(validator::validateRow)).toList();

        return valid.stream().collect(Stats.collector(rows.size(), valid.size(), System.nanoTime() - start, limit));
    }

    public static <T> Collector<MultipartFile, ?, List<T>> listCollector(CSVFormat format, Function<CSVRecord, T> mapper) {
        return Collectors.flatMapping(multiFile -> {
            try(var parser = format.parse(new InputStreamReader(multiFile.getInputStream()))) {
                return parser.getRecords().stream().map(mapper);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Collectors.toList());
    }
}
