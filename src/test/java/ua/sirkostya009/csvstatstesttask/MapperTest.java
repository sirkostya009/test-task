package ua.sirkostya009.csvstatstesttask;

import jakarta.annotation.Resource;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MapperTest {
    @Resource
    private Mapper mapper;

    @Test
    public void testToRow() throws Exception {
        var record = newRecord(new String[]{
                "127.0.0.1",
                "01/01/1970:00:00:00-0000",
                "GET",
                "/",
                "200"
        });

        var row = mapper.toRow(record);

        assertThat(row).isNotNull();
    }

    private CSVRecord newRecord(String[] records) throws Exception {
        var recordConstructor = CSVRecord.class.getDeclaredConstructor(CSVParser.class, String[].class, String.class, long.class, long.class);
        recordConstructor.setAccessible(true);

        return recordConstructor.newInstance(
                null,
                records,
                null,
                0,
                0
        );
    }
}
