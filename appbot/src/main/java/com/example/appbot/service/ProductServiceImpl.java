package com.example.appbot.service;

import com.example.appbot.util.FileUtil;
import com.example.appbot.dao.ProductDao;
import com.example.appbot.dto.ProductDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private final S3Service s3Service;

    public ProductServiceImpl(ProductDao productDao, S3Service s3Service) {
        this.productDao = productDao;
        this.s3Service = s3Service;
    }

    @Override
    public List<ProductDTO> findProductById(Integer id) {
        return productDao.findProductById(id);
    }

    @Override
    public List<ProductDTO> findAllProduct() {
        return productDao.findAllProduct();
    }

    @Override
    public List<Integer> createProducts(List<ProductDTO> productDTOList, List<MultipartFile> images) {
        List<Integer> productIds = new ArrayList<>();

        for (int i = 0; i < productDTOList.size(); i++) {
            ProductDTO productDTO = productDTOList.get(i);
            MultipartFile image = images.get(i);

            try {
                String uuidFileName = FileUtil.generateUuidFileName(image.getOriginalFilename());
                s3Service.uploadFile(image, uuidFileName);
                productDTO.setImage(uuidFileName);
                Integer productId = productDao.createProduct(productDTO);
                productIds.add(productId);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("圖片上傳失敗", e);
            }
        }

        return productIds;
    }
}
