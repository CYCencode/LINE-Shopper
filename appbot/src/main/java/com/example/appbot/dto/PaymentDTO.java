package com.example.appbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private Integer id;
    private String method;
    private String recTradeId;
    private String bankTransactionId;
    private String bankOrderNumber;
    private String authCode;
    private Integer amount;
    private String currency;
    private String transactionTime;
    private String bankResultCode;
    private String bankResultMsg;
    private String cardIdentifier;
}
