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
public class PayByPrimeDTO {
    private String prime;
    @JsonProperty("partner_key")
    private String partnerKey;
    @JsonProperty("merchant_id")
    private String merchantId;
    private Integer amount;
    private String details;
    private CardHolderDTO cardholder;
    @JsonProperty("three_domain_secure")
    private Boolean threeDomainSecure;
}