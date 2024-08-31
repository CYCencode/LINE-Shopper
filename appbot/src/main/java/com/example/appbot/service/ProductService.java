package com.example.appbot.service;

import com.example.appbot.dto.ProductDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findProductById(Integer id);
    List<ProductDTO> findAllProduct();
}
