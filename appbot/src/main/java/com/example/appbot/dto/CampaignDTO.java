package com.example.appbot.dto;

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
    private Float discountRate;
}
