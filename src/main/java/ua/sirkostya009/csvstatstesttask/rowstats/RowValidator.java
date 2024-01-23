package ua.sirkostya009.csvstatstesttask.rowstats;

import org.springframework.stereotype.Component;
import ua.sirkostya009.csvstatstesttask.service.Validator;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
public class RowValidator implements Validator<Row> {
    private final Pattern ipPattern = Pattern.compile("^(?!0+\\.)\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
    private final Pattern datePattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}:\\d{2}:\\d{2}:\\d{2}-\\d{4}$");
    private final Pattern methodPattern = Pattern.compile("^GET|POST|PUT|DELETE|HEAD|OPTIONS|TRACE|CONNECT$");
    private final Pattern uriPattern = Pattern.compile("^/\\S*$");
    private final Pattern statusPattern = Pattern.compile("^[1-5]\\d{2}$");

    @Override
    public boolean test(Row row) {
        var ip = ipPattern.matcher(row.ip());
        var date = datePattern.matcher(row.date());
        var method = methodPattern.matcher(row.method());
        var uri = uriPattern.matcher(row.uri());
        var status = statusPattern.matcher(row.status());

        return ip.matches() &&
               date.matches() &&
               method.matches() &&
               uri.matches() &&
               status.matches() &&
               Arrays.stream(row.ip().split("\\."))
                       .mapToInt(Integer::parseInt)
                       .allMatch(i -> i < 256);
    }
}
