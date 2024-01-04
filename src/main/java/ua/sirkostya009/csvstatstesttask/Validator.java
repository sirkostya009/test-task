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

    /**
     * Wraps a validator method with a try-catch block.
     * <p>
     * Since the Validator class is annotated with @Validated, some of its methods may throw exception,
     * which is not expected of a Predicate. This method wraps the validator method with a try-catch block
     * to make it suitable for using as a stable Predicate.
     * @param validator the proxied validation method
     * @return a non-throwing predicate
     */
    public static <T> Predicate<T> normalize(Predicate<T> validator) {
        return t -> {
            try {
                return validator.test(t);
            } catch (Exception ignored) {
                return false;
            }
        };
    }
}
