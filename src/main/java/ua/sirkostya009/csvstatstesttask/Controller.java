package ua.sirkostya009.csvstatstesttask;

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

    @PostMapping("/parse")
    public Stats upload(@RequestPart("files") MultipartFile[] files,
                        @RequestParam(defaultValue = "5") int limit) {
        var start = System.nanoTime();

        var rows = Arrays.stream(files).collect(Mapper.listCollector(CSVFormat.newFormat(';'), mapper::toRow));

        var valid = rows.stream().filter(validator::validateRow).toList();

        return valid.stream().collect(Stats.collector(rows.size(), valid.size(), System.nanoTime() - start, limit));
    }
}
