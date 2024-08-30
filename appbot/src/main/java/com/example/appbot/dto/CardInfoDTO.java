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
public class CardInfoDTO {
    @JsonProperty("bin_code")
    private String binCode;
    @JsonProperty("last_four")
    private String lastFour;
    private String issuer;
    @JsonProperty("issuer_zh_tw")
    private String issuerZhTw;
    @JsonProperty("bank_id")
    private String bankId;
    private Integer funding;
    private Integer type;
    private String level;
    private String country;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("expiry_date")
    private String expiryDate;
}
