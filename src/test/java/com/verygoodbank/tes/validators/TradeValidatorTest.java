package com.verygoodbank.tes.validators;

import com.verygoodbank.tes.web.dto.TradeDataRequestDto;
import com.verygoodbank.tes.web.validators.impl.TradeValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TradeValidatorTest {
    @InjectMocks
    private TradeValidatorImpl tradeValidator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidTrade() {
        TradeDataRequestDto validTrade = new TradeDataRequestDto(
                "20230101",
                1,
                "USD",
                100.0);
        assertTrue(tradeValidator.isValidTrade(validTrade));
    }

    @Test
    public void testInvalidTrade() {
        TradeDataRequestDto invalidTrade = new TradeDataRequestDto(
                "2023-01-01",
                1,
                "USD",
                100.0
        );
        assertFalse(tradeValidator.isValidTrade(invalidTrade));
    }

    @Test
    public void testInvalidDate() {
        assertFalse(tradeValidator.isValidTrade(new TradeDataRequestDto(
                "invalid_date",
                1,
                "USD",
                100.0
        )));
    }

    @Test
    public void testEmptyDate() {
        assertFalse(tradeValidator.isValidTrade(new TradeDataRequestDto(
                "",
                1,
                "USD",
                100.0
        )));
    }

    @Test
    public void testNullDate() {
        assertFalse(tradeValidator.isValidTrade(new TradeDataRequestDto(
                null,
                1,
                "USD",
                100.0
        )));
    }
}
