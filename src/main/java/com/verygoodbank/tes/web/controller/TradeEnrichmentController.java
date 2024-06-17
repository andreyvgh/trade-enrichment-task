package com.verygoodbank.tes.web.controller;


import com.verygoodbank.tes.web.dto.EnrichedTradeDataDto;
import com.verygoodbank.tes.web.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class TradeEnrichmentController {
    private final TradeService tradeService;

    @PostMapping(value = "/enrich", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "text/csv")
    public ResponseEntity<StreamingResponseBody> enrichTrades(@RequestParam("file") MultipartFile file,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "100") int size
    ) {
        try {
            StreamingResponseBody responseBody = getStreamingResponseBody(file, page, size);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                    .body(responseBody);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private StreamingResponseBody getStreamingResponseBody(MultipartFile file, int page, int size) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        return outputStream -> {
            PrintWriter writer = new PrintWriter(outputStream);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                    "date",
                    "product_name",
                    "currency",
                    "amount")
            );

            List<EnrichedTradeDataDto> enrichedTrades = tradeService.enrichTrades(reader, page, size);
            for (EnrichedTradeDataDto trade : enrichedTrades) {
                csvPrinter.printRecord(trade.date(), trade.productName(), trade.currency(), trade.amount());
            }
            csvPrinter.flush();
        };
    }
}


