package ua.sirkostya009.csvstatstesttask;

import org.assertj.core.api.Condition;
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
public class ControllerTests {
    @Autowired
    private Controller controller;

    @Test
    public void testUpload() {
        var files = getFiles("valid.csv", "invalid-codes.csv", "invalid-ips.csv", "invalid-methods.csv");

        var stats = controller.upload(files, 2);

        assertThat(stats)
                .is(new Condition<>(s -> s.getValidRows() == 41, "valid"))
                .is(new Condition<>(s -> s.getTotalRows() == 45, "total"))
                .is(new Condition<>(s -> Map.of(
                        "GET@/api/resource", 14L,
                        "POST@/api/data", 15L
                ).equals(s.getTopUris()), "top uris"));
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
