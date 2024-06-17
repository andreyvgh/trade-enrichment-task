package com.verygoodbank.tes.web.validators.impl;

import com.verygoodbank.tes.web.dto.TradeDataRequestDto;
import com.verygoodbank.tes.web.validators.TradeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
public class TradeValidatorImpl implements TradeValidator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public boolean isValidTrade(TradeDataRequestDto trade) {
        if (!isValidDate(trade.date())) {
            log.error("Invalid date format: " + trade.date());
            return false;
        }
        return true;
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }
}
