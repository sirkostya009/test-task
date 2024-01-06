package ua.sirkostya009.csvstatstesttask;

import jakarta.validation.constraints.Pattern;
import org.apache.commons.csv.CSVRecord;

public record Row(
        @Pattern(regexp = "^(?!000)(?!00\\.)(?!0\\.)(\\d{1,3})\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")
        String ip,
        @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}:\\d{2}:\\d{2}:\\d{2}-\\d{4}$")
        String date,
        @Pattern(regexp = "^GET|POST|PUT|DELETE|HEAD|OPTIONS|TRACE|CONNECT$")
        String method,
        @Pattern(regexp = "^/\\S*$")
        String uri,
        @Pattern(regexp = "^[1-5]\\d{2}$")
        String status
) {
    public static Row of(CSVRecord record) {
        return new Row(
                record.get(0),
                record.get(1),
                record.get(2),
                record.get(3),
                record.get(4)
        );
    }
}
