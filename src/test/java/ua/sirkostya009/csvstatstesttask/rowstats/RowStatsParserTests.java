package ua.sirkostya009.csvstatstesttask.rowstats;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RowStatsParserTests {
    @Autowired
    private RowStatsParser service;

    @Test
    public void testUpload() {
        var topUris = Map.of("POST@/api/data", 15L,
                             "GET@/api/resource", 14L);

        var files = getFiles("valid.csv", "invalid-codes.csv", "invalid-ips.csv", "invalid-methods.csv");

        var stats = service.apply(files, Map.of("limit", "2"));

        assertThat(stats)
                .hasFieldOrPropertyWithValue("validRows", 41)
                .hasFieldOrPropertyWithValue("totalRows", 45)
                .hasFieldOrPropertyWithValue("topUris", topUris);
    }

    private MultipartFile[] getFiles(String... names) {
        return Arrays.stream(names).map(name -> {
            try {
                return new MockMultipartFile(name, name, "text/csv", getClass().getResourceAsStream("/" + name));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toArray(MultipartFile[]::new);
    }
}
