package com.example.appbot.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampaignDTO {
    private Integer id;
    private Integer productId;
    private String name;
    private String createAt;
    private String terminateAt;
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private Float discountRate;
}
