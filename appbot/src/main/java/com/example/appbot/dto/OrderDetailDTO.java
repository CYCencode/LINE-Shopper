package com.example.appbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDTO {
    private Integer id;
    private Integer quantity;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private Integer campaignId;
    private Integer originalPrice;
    private Integer discountedPrice;
}