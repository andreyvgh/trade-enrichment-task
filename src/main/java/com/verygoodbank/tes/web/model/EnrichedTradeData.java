package com.verygoodbank.tes.web.model;

import lombok.Data;

@Data
public class EnrichedTradeData {
    private String date;
    private String productName;
    private String currency;
    private double amount;
}
