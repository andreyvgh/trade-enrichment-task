package com.verygoodbank.tes.web.validators;

import com.verygoodbank.tes.web.dto.TradeDataRequestDto;


public interface TradeValidator {
    boolean isValidTrade(TradeDataRequestDto trade);
}
