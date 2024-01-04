package ua.sirkostya009.csvstatstesttask;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
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
}
