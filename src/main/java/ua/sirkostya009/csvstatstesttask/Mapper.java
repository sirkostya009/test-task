package ua.sirkostya009.csvstatstesttask;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

@Service
public class Mapper {
    private final static int IP = 0;
    private final static int DATE = 1;
    private final static int METHOD = 2;
    private final static int URI = 3;
    private final static int STATUS = 4;

    public Row toRow(CSVRecord record) {
        return new Row(
                record.get(IP),
                record.get(DATE),
                record.get(METHOD),
                record.get(URI),
                record.get(STATUS)
        );
    }

    public static <T> Collector<MultipartFile, ?, List<T>> listCollector(CSVFormat format, Function<CSVRecord, T> mapper) {
        return Collector.of(ArrayList::new,
                            (list, file) -> {
                                try(var parser = format.parse(new InputStreamReader(file.getInputStream()))) {
                                    parser.getRecords().stream().map(mapper).forEach(list::add);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            (list, _l) -> list);
    }
}
