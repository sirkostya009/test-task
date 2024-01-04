package ua.sirkostya009.csvstatstesttask;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.function.Predicate;

@Component
@Validated
public class Validator {
    public boolean validateRow(@Valid Row row) {
        try {
            var bytes = Arrays.stream(row.ip().split("\\.")).mapToInt(Integer::parseInt).toArray();
            int status = row.status().charAt(0) - '0';

            return bytes[0] != 0 && Arrays.stream(bytes).allMatch(b -> b < 256)
                    && status >= 1 && status <= 5;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static <T> Predicate<T> proxiedPredicate(Predicate<T> validator) {
        return t -> {
            try {
                return validator.test(t);
            } catch (Exception ignored) {
                return false;
            }
        };
    }
}
