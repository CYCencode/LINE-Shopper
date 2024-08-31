package com.example.appbot.service;

import com.example.appbot.dao.ProductDao;
import com.example.appbot.dto.ProductDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public List<ProductDTO> findProductById(Integer id) {
        return productDao.findProductById(id);
    }

    @Override
    public List<ProductDTO> findAllProduct() {
        return productDao.findAllProduct();
    }
}
