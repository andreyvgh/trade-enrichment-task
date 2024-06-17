package com.verygoodbank.tes.Integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class TradeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void enrichTrades() throws Exception {
        ClassPathResource resource = new ClassPathResource("trade.csv");
        String csvInput = readCsvFromResource(resource);

        MockMultipartFile file = new MockMultipartFile("file",
                "trade.csv",
                "text/csv",
                csvInput.getBytes()
        );

        String expectedCsvOutput = """
                date,product_name,currency,amount\r
                20160101,Treasury Bills Domestic,EUR,10.0\r
                20160101,Corporate Bonds Domestic,EUR,20.1\r
                20160101,REPO Domestic,EUR,30.34\r
                20160101,Missing Product Name,EUR,35.34\r
                """;

        mockMvc.perform(multipart("/api/v1/enrich")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedCsvOutput));
    }


    private String readCsvFromResource(ClassPathResource resource) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(),
                StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }
}
