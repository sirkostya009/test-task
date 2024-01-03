package ua.sirkostya009.csvstatstesttask;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record Stats(
        Map<String, Long> topUris,
        Map<String, Long> requestsPerSecond,
        int totalRows,
        int validRows,
        Instant parseTime
) {
}
