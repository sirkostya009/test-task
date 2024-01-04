package ua.sirkostya009.csvstatstesttask;

import jakarta.annotation.Resource;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ValidatorTests {
    @Resource
    private Validator validator;

    @Test
    public void testValidateRow() {
        var row = new Row(
                "127.0.0.1",
                "01/01/1970:00:00:00-0000",
                "GET",
                "/",
                "200"
        );

        assertThat(validator.validateRow(row)).isEqualTo(true);
    }

    @Test
    public void testValidateRowInvalidIp() {
        var row = new Row(
                ".0.0.256",
                "01/01/1970:00:00:00-0000",
                "GET",
                "/",
                "200"
        );

        assertThatThrownBy(() -> validator.validateRow(row))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void testValidateRowInvalidIpCustomCheck() {
        var row = new Row(
                "0.0.0.256",
                "01/01/1970:00:00:00-0000",
                "GET",
                "/",
                "300"
        );

        assertThat(validator.validateRow(row)).isEqualTo(false);
    }
}
