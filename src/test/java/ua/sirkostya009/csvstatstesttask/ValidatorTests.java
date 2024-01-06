package ua.sirkostya009.csvstatstesttask;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ValidatorTests {
    @Autowired
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
    public void testValidateRowHibernateValidationFailed() {
        var row = new Row(
                "0.133.0.256",
                "01/01/1970:00:00:00-0000",
                "GET",
                "/",
                "900"
        );

        assertThatThrownBy(() -> validator.validateRow(row))
                .hasMessageContainingAll(
                        "ip: must match \"^(?!0+\\.)\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$\"",
                        "status: must match \"^[1-5]\\d{2}$\""
                );
    }

    @Test
    public void testValidateRowCustomValidation() {
        var row = new Row(
                "192.300.0.256",
                "01/01/1970:00:00:00-0000",
                "GET",
                "/",
                "300"
        );

        assertThat(validator.validateRow(row)).isEqualTo(false);
    }
}
