package ua.sirkostya009.csvstatstesttask;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
public class Validator {
    public boolean validateRow(Row row) {
        try {
            var bytes = Arrays.stream(row.ip().split("\\.")).mapToInt(Integer::parseInt).toArray();
            int status = row.status().charAt(0) - '0';

            return Pattern.matches("^\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}$", row.ip())
                    && Pattern.matches("^\\d{2}/\\d{2}/\\d{4}:\\d{2}:\\d{2}:\\d{2}-\\d{4}$", row.date())
                    && Pattern.matches("^GET|POST|PUT|DELETE|HEAD|OPTIONS|TRACE|CONNECT$", row.method())
                    && Pattern.matches("^/.*+$", row.uri())
                    && Pattern.matches("^\\d{3}$", row.status())
                    && bytes[0] != 0 && Arrays.stream(bytes).allMatch(b -> b < 256)
                    && status >= 1 && status <= 5;
        } catch (Exception ignored) {
            return false;
        }
    }
}
