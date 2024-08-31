package com.example.appbot.dao;

import com.example.appbot.dto.ProductDTO;

import java.util.List;

public interface ProductDao {
    List<ProductDTO> findAllProduct();
    List<ProductDTO> findProduct(Integer limit);
    List<ProductDTO> findProductByCategory(Integer limit, String category);
    List<ProductDTO> findProductByKeyword(String keyword);
    Integer findProductPrice(Integer productId);
    List<ProductDTO> findProductById(Integer productId);
    List<ProductDTO> findCampaign();
}