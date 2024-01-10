package ua.sirkostya009.csvstatstesttask;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.sirkostya009.csvstatstesttask.model.Row;
import ua.sirkostya009.csvstatstesttask.validator.RowValidator;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RowValidatorTests {
    @Autowired
    private RowValidator validator;

    @Test
    public void testValidateRow() {
        var row = new Row(
                "127.0.0.1",
                "01/01/1970:00:00:00-0000",
                "GET",
                "/",
                "200"
        );

        assertThat(validator.test(row)).isEqualTo(true);
    }

    @Test
    public void testInvalidIpAndStatus() {
        var row = new Row(
                "0.133.0.256",
                "01/01/1970:00:00:00-0000",
                "GET",
                "/",
                "900"
        );

        assertThat(validator.test(row)).isEqualTo(false);
    }
}
