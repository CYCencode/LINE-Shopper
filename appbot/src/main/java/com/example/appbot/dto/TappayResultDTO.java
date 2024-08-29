package com.example.appbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TappayResultDTO {
    private Integer status;
    private String msg;
    @JsonProperty("rec_trade_id")
    private String recTradeId;
    @JsonProperty("bank_transaction_id")
    private String bankTransactionId;
    @JsonProperty("bank_order_number")
    private String bankOrderNumber;
    @JsonProperty("auth_code")
    private String authCode;
    //    private String cardSecret;
    private Integer amount;
    private String currency;
    //    private String cardInfo;
    @JsonProperty("order_number")
    private String orderNumber;
    private String acquirer;
    @JsonProperty("transaction_time_millis")
    private Long transactionTimeMillis;
    @JsonProperty("bank_result_code")
    private String bankResultCode;
    @JsonProperty("bank_result_msg")
    private String bankResultMsg;
    @JsonProperty("payment_url")
    private String paymentUrl;
    @JsonProperty("card_identifier")
    private String cardIdentifier;

}
