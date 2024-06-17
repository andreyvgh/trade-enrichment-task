package com.verygoodbank.tes.service;

import com.verygoodbank.tes.web.dto.EnrichedTradeDataDto;
import com.verygoodbank.tes.web.dto.TradeDataRequestDto;
import com.verygoodbank.tes.web.repository.ProductRepository;
import com.verygoodbank.tes.web.service.TradeService;
import com.verygoodbank.tes.web.service.impl.TradeServiceImpl;
import com.verygoodbank.tes.web.validators.TradeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TradeServiceTest {

    private TradeService tradeService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TradeValidator tradeValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tradeService = new TradeServiceImpl(productRepository, tradeValidator);
    }

    @Test
    void getEnrichedTradesShouldReturnCorrectNames() throws IOException {
        String csvData = """
                date,product_id,currency,price
                20160101,1,EUR,10.0
                20160101,2,EUR,20.1
                20160101,11,EUR,35.34""";
        BufferedReader reader = new BufferedReader(new StringReader(csvData));

        TradeDataRequestDto tradeRequestDto1 = new TradeDataRequestDto("20160101", 1, "EUR", 10.0);
        TradeDataRequestDto tradeRequestDto2 = new TradeDataRequestDto("20160101", 2, "EUR", 20.1);
        TradeDataRequestDto tradeRequestDto3 = new TradeDataRequestDto("20160101", 11, "EUR", 35.34);
        when(tradeValidator.isValidTrade(tradeRequestDto1))
                .thenReturn(true);
        when(tradeValidator.isValidTrade(tradeRequestDto2))
                .thenReturn(true);
        when(tradeValidator.isValidTrade(tradeRequestDto3))
                .thenReturn(true);
        when(productRepository.getProductName(1)).thenReturn("Treasury Bills Domestic");
        when(productRepository.getProductName(2)).thenReturn("Corporate Bonds Domestic");
        when(productRepository.getProductName(11)).thenReturn("Missing Product Name");

        List<EnrichedTradeDataDto> enrichedTrades = tradeService.enrichTrades(reader, 0, 100);

        assertEquals(3, enrichedTrades.size());
        assertEquals("Treasury Bills Domestic", enrichedTrades.get(0).productName());
        assertEquals("Corporate Bonds Domestic", enrichedTrades.get(1).productName());
        assertEquals("Missing Product Name", enrichedTrades.get(2).productName());

    }

    @Test
    void getEnrichedTradesShouldHandleInvalidDates() throws IOException {
        String csvData = """
                date,product_id,currency,price
                20160101,1,EUR,10.0
                invalid_date,2,EUR,20.1
                20160101,11,EUR,35.34""";
        BufferedReader reader = new BufferedReader(new StringReader(csvData));

        TradeDataRequestDto tradeRequestDto1 = new TradeDataRequestDto("20160101", 1, "EUR", 10.0);
        TradeDataRequestDto tradeRequestDto2 = new TradeDataRequestDto("invalid_date", 2, "EUR", 20.1);
        TradeDataRequestDto tradeRequestDto3 = new TradeDataRequestDto("20160101", 11, "EUR", 35.34);
        when(tradeValidator.isValidTrade(tradeRequestDto1)).thenReturn(true);
        when(tradeValidator.isValidTrade(tradeRequestDto2)).thenReturn(false);
        when(tradeValidator.isValidTrade(tradeRequestDto3)).thenReturn(true);
        when(productRepository.getProductName(1)).thenReturn("Treasury Bills Domestic");
        when(productRepository.getProductName(11)).thenReturn("Missing Product Name");

        List<EnrichedTradeDataDto> enrichedTrades = tradeService.enrichTrades(reader, 0, 100);

        assertEquals(2, enrichedTrades.size());
        assertEquals("Treasury Bills Domestic", enrichedTrades.get(0).productName());
        assertEquals("Missing Product Name", enrichedTrades.get(1).productName());
    }

    @Test
    void getEnrichedTradesShouldHandlePagination() throws IOException {
        String csvData = """
                date,product_id,currency,price
                20160101,1,EUR,10.0
                20160101,2,EUR,20.1
                20160101,3,EUR,30.34
                20160101,11,EUR,35.34""";
        BufferedReader reader = new BufferedReader(new StringReader(csvData));

        TradeDataRequestDto tradeRequestDto1 = new TradeDataRequestDto("20160101", 1, "EUR", 10.0);
        TradeDataRequestDto tradeRequestDto2 = new TradeDataRequestDto("20160101", 2, "EUR", 20.1);
        TradeDataRequestDto tradeRequestDto3 = new TradeDataRequestDto("20160101", 3, "EUR", 30.34);
        TradeDataRequestDto tradeRequestDto4 = new TradeDataRequestDto("20160101", 11, "EUR", 35.34);
        when(tradeValidator.isValidTrade(tradeRequestDto1)).thenReturn(true);
        when(tradeValidator.isValidTrade(tradeRequestDto2)).thenReturn(true);
        when(tradeValidator.isValidTrade(tradeRequestDto3)).thenReturn(true);
        when(tradeValidator.isValidTrade(tradeRequestDto4)).thenReturn(true);
        when(productRepository.getProductName(1)).thenReturn("Treasury Bills Domestic");
        when(productRepository.getProductName(2)).thenReturn("Corporate Bonds Domestic");
        when(productRepository.getProductName(3)).thenReturn("REPO Domestic");
        when(productRepository.getProductName(11)).thenReturn("Missing Product Name");

        List<EnrichedTradeDataDto> firstPage = tradeService.enrichTrades(reader, 0, 2);
        assertEquals(2, firstPage.size());
        assertEquals("Treasury Bills Domestic", firstPage.get(0).productName());
        assertEquals("Corporate Bonds Domestic", firstPage.get(1).productName());

        reader = new BufferedReader(new StringReader(csvData)); // Reset reader for next page
        List<EnrichedTradeDataDto> secondPage = tradeService.enrichTrades(reader, 1, 2);
        assertEquals(2, secondPage.size());
        assertEquals("REPO Domestic", secondPage.get(0).productName());
        assertEquals("Missing Product Name", secondPage.get(1).productName());
    }

}
