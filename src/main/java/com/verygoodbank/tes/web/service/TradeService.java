package com.verygoodbank.tes.web.service;

import com.verygoodbank.tes.web.dto.EnrichedTradeDataDto;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public interface TradeService {
    List<EnrichedTradeDataDto> enrichTrades(BufferedReader reader, int page, int size) throws IOException;
}
