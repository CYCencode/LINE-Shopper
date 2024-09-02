package com.example.appbot.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private Integer id;
    @Min(value = 1, message = "價格必須大於或等於1")
    private Integer price;
    @Min(value = 1, message = "庫存必須大於或等於1")
    private Integer stock;
    private String category;
    private String name;
    private String image;
    private MultipartFile imageFile;
}
