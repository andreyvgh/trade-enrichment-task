package com.verygoodbank.tes.web.model;

import lombok.Data;

@Data
public class TradeData {
    private String date;
    private int productId;
    private String currency;
    private double amount;
}
