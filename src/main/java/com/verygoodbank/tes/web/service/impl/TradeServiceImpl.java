package com.verygoodbank.tes.web.service.impl;

import com.verygoodbank.tes.web.dto.EnrichedTradeDataDto;
import com.verygoodbank.tes.web.dto.TradeDataRequestDto;
import com.verygoodbank.tes.web.model.TradeData;
import com.verygoodbank.tes.web.repository.ProductRepository;
import com.verygoodbank.tes.web.service.TradeService;
import com.verygoodbank.tes.web.validators.TradeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final ProductRepository productRepository;

    private final TradeValidator tradeValidator;

    @Override
    public List<EnrichedTradeDataDto> enrichTrades(BufferedReader reader, int page, int size) {
        int start = page * size;

        try (Stream<String> lines = reader.lines().skip(1)) {
            List<TradeDataRequestDto> validTrades = lines
                    .skip(start)
                    .limit(size)
                    .map(line -> {
                        String[] values = line.split(",");
                        return new TradeDataRequestDto(values[0], Integer.parseInt(values[1]), values[2], Double.parseDouble(values[3]));
                    })
                    .filter(tradeValidator::isValidTrade)
                    .toList();

            List<TradeData> trades = validTrades.stream()
                    .map(this::convertToTrade)
                    .toList();

            return trades.stream()
                    .map(this::enrichTrade)
                    .toList();
        }

    }

    private EnrichedTradeDataDto enrichTrade(TradeData trade) {
        String productName = productRepository.getProductName(trade.getProductId());
        if ("Missing Product Name".equals(productName)) {
            log.error("Missing product name for productId: " + trade.getProductId());
        }
        return new EnrichedTradeDataDto(trade.getDate(), productName, trade.getCurrency(), trade.getAmount());
    }

    private TradeData convertToTrade(TradeDataRequestDto dto) {
        TradeData trade = new TradeData();
        trade.setDate(dto.date());
        trade.setProductId(dto.productId());
        trade.setCurrency(dto.currency());
        trade.setAmount(dto.amount());
        return trade;
    }
}
