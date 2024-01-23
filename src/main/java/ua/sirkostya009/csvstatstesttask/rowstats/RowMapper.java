package ua.sirkostya009.csvstatstesttask.rowstats;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import ua.sirkostya009.csvstatstesttask.service.Mapper;

@Component
public class RowMapper implements Mapper<Row> {
    private final int ip = 0;
    private final int date = 1;
    private final int method = 2;
    private final int uri = 3;
    private final int status = 4;

    @Override
    public Row apply(CSVRecord record) {
        return new Row(
                record.get(ip),
                record.get(date),
                record.get(method),
                record.get(uri),
                record.get(status)
        );
    }
}
