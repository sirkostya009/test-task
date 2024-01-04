package ua.sirkostya009.csvstatstesttask;

public record Row(
        String ip,
        String date,
        String method,
        String uri,
        String status
) {
}
