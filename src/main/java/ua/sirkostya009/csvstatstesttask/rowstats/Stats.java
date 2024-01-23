package ua.sirkostya009.csvstatstesttask.rowstats;

import lombok.Builder;

import java.util.Map;

@Builder
public record Stats(
        Map<String, Long> topUris,
        Map<String, Long> requestsPerSecond,
        int totalRows,
        int validRows,
        long parseTime
) {
}
