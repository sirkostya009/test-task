package ua.sirkostya009.csvstatstesttask.service;

import org.apache.commons.csv.CSVFormat;
import org.springframework.web.multipart.MultipartFile;
import ua.sirkostya009.csvstatstesttask.mapper.Mapper;
import ua.sirkostya009.csvstatstesttask.validator.Validator;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface ParseService<T, R> extends BiFunction<MultipartFile[], Map<String, Object>, R> {
    CSVFormat getFormat();

    Mapper<T> getMapper();

    default List<T> parseRows(MultipartFile[] files) {
        return Arrays.stream(files)
                .flatMap(file -> {
                    try(var parser = getFormat().parse(new InputStreamReader(file.getInputStream()))) {
                        return parser.getRecords().stream();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(getMapper())
                .toList();
    }

    Validator<T> getValidator();

    default List<T> filterValidRows(List<T> rows) {
        return rows.stream().filter(getValidator()).toList();
    }
}
