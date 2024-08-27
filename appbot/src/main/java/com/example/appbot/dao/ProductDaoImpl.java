package com.example.appbot.dao;

import com.example.appbot.dto.ProductDTO;
import com.example.appbot.rowmapper.ProductDTORowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
public class ProductDaoImpl implements ProdcutDao{

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Override
    public List<ProductDTO> findProduct(Integer limit) {
        String sql = "select * from products limit :limit";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);

        return template.query(sql, params, new ProductDTORowMapper());
    }

    @Override
    public List<ProductDTO> findProductByCategory(Integer limit, String category) {
        String sql = "select * from products where category=:category limit :limit";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);
        params.addValue("category", category);

        return template.query(sql, params, new ProductDTORowMapper());
    }
}
