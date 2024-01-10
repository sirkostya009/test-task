package ua.sirkostya009.csvstatstesttask.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.sirkostya009.csvstatstesttask.service.ParseService;
import ua.sirkostya009.csvstatstesttask.model.Stats;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final ParseService service;

    @Operation(
            summary = "Parse CSV files and return statistics",
            description = "Only returns an error if something went wrong internally, i.e., not client's fault",
            parameters = @Parameter(name = "limit", description = "The number of top URIs to collect"),
            requestBody = @RequestBody(
                    description = "CSV files to parse. Note that header is not required",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schemaProperties = @SchemaProperty(
                                    name = "files",
                                    array = @ArraySchema(items = @Schema(
                                            format = "binary",
                                            type = "string"
                                    ))
                            ),
                            examples = @ExampleObject(
                                    summary = "example csv file contents",
                                    value = """
                                            192.168.2.212;28/07/2006:10:27:10-0300;GET;/user/try/;200
                                            192.168.2.212;28/07/2006:10:22:04-0300;GET;/;200
                                            192.168.2.220;28/07/2006:10:25:04-0300;PUT;/save/;200
                                            192.168.2.111;28/07/2006:10:25:04-0300;PUT;/save/;403"""
                            ),
                            encoding = @Encoding(name = "files", contentType = "text/csv")
                    )
            ),
            tags = {"CSV"}
    )
    @PostMapping(path = "/parse", consumes = "multipart/form-data", produces = "application/json")
    public Stats upload(@RequestPart("files") MultipartFile[] files,
                        @RequestParam(defaultValue = "5") int limit) {
        return service.parse(files, limit);
    }
}
