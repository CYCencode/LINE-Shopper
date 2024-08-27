package com.example.appbot.dao;

import com.example.appbot.dto.ProductDTO;

import java.util.List;

public interface ProdcutDao {
    List<ProductDTO> findProduct(Integer limit);

    List<ProductDTO> findProductByCategory(Integer limit, String category);
}
