package ua.sirkostya009.csvstatstesttask;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class StatsTest {
    @Test
    public void testCollector() {
        assertThat(Stream.of(
                new Row(
                        "127.0.0.1",
                        "01/01/1970:00:00:00-0000",
                        "GET",
                        "/",
                        "200"
                ),
                new Row(
                        "255.255.255.0",
                        "01/01/1970:00:00:00-0000",
                        "GET",
                        "/users",
                        "200"
                ),
                new Row(
                        "55.127.156.3",
                        "01/01/1970:00:00:00-0000",
                        "POST",
                        "/users",
                        "201"
                )
        ).collect(Stats.collector(100, 5, 100, 2)))
                .is(new Condition<>(s -> s.getTopUris().size() == 2, "topUris.size() == 2"))
                .is(new Condition<>(s -> Map.of("01/01/1970:00:00:00-0000", 3L).equals(s.getRequestsPerSecond()), "requestsPerSecond.equals"))
                .is(new Condition<>(s -> s.getTotalRows() == 100, "totalRows == 100"))
                .is(new Condition<>(s -> s.getValidRows() == 5, "validRows == 5"))
                .is(new Condition<>(s -> s.getParseTime() == 100, "parseTime == 100"));
    }
}
