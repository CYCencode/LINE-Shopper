package com.example.appbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private Integer id;
    private Integer price;
    private Integer discountPrice;
    private Integer stock;
    private String category;
    private String name;
    private String image;
}
