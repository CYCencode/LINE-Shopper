package com.example.appbot.rowmapper;

import com.example.appbot.dto.ProductDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDTORowMapper implements RowMapper<ProductDTO> {
    @Override
    public ProductDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProductDTO.builder()
            .id(rs.getInt("id"))
            .price(rs.getInt("price"))
            .stock(rs.getInt("stock"))
            .category(rs.getString("category"))
            .name(rs.getString("name"))
            .image(rs.getString("image"))
            .build();
    }
}
