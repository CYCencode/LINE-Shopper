package com.example.appbot.dto;

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
    private Integer price;
    private Integer stock;
    private String category;
    private String name;
    private String image;
    private MultipartFile imageFile;
}
