package ua.sirkostya009.csvstatstesttask.rowstats;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RowMapperTests {
    @Autowired
    private RowMapper mapper;

    @Test
    void testMappedRow() throws Exception {
        var ip = "127.0.0.1";
        var date = "01/01/1970:00:00:00-0000";
        var method = "GET";
        var uri = "/";
        var status = "200";

        var record = newRecord(ip, date, method, uri, status);
        var row = mapper.apply(record);

        assertThat(row)
                .hasFieldOrPropertyWithValue("ip", ip)
                .hasFieldOrPropertyWithValue("date", date)
                .hasFieldOrPropertyWithValue("method", method)
                .hasFieldOrPropertyWithValue("uri", uri)
                .hasFieldOrPropertyWithValue("status", status);
    }

    private CSVRecord newRecord(String... values) throws Exception {
        var constructor = CSVRecord.class.getDeclaredConstructor(CSVParser.class, String[].class, String.class, long.class, long.class);
        constructor.setAccessible(true);

        return constructor.newInstance(null, values, null, 0, 0);
    }
}
