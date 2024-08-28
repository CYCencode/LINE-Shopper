package com.example.appbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequestDTO {
    private String prime;

    @NotNull
    @JsonProperty("payment_method")
    private String paymentMethod;

    @NotBlank
    @JsonProperty("line_user_id")
    private String lineUserId;

    @NotNull
    @JsonProperty("product_id")
    private Integer productId;

    @NotNull
    @JsonProperty("receiver_name")
    private String receiverName;

    @NotNull
    @JsonProperty("receiver_phone")
    private String receiverPhone;

    @NotNull
    @JsonProperty("receiver_address")
    private String receiverAddress;

    @NotNull
    @JsonProperty("receiver_zipcode")
    private String receiverZipcode;

    @Email
    @JsonProperty("receiver_email")
    private String receiverEmail;
}
