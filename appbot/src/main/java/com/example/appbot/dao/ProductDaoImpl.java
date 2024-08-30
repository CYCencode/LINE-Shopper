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
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Override
    public List<ProductDTO> findProduct(Integer limit) {
        String sql = "SELECT * FROM products LIMIT :limit";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);

        return template.query(sql, params, new ProductDTORowMapper());
    }

    @Override
    public List<ProductDTO> findProductByCategory(Integer limit, String category) {
        String sql = "SELECT * FROM products WHERE category=:category LIMIT :limit";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);
        params.addValue("category", category);

        return template.query(sql, params, new ProductDTORowMapper());
    }

    @Override
    public List<ProductDTO> findProductById(Integer productId) {
        String sql = "SELECT * FROM products WHERE id =:productId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productId", productId);

        return template.query(sql, params, new ProductDTORowMapper());
    }

    @Override
    public List<ProductDTO> findProductByKeyword(String keyword) {
        String sql = "SELECT * FROM products WHERE name LIKE :keyword LIMIT 3";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("keyword", "%" + keyword + "%");

        return template.query(sql, params, new ProductDTORowMapper());
    }

    @Override
    public Integer findProductPrice(Integer productId) {
        String sql = "SELECT price FROM products WHERE id=:productId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productId", productId);
        return template.queryForObject(sql, params, Integer.class);
    }

    ;

    @Override
    public List<ProductDTO> findCampaign() {
        String sql = "SELECT p.id, p.name, (p.price*c.discount_rate) AS price, p.image " +
                "FROM products p " +
                "JOIN campaigns c ON c.product_id = p.id " +
                "WHERE c.create_at <= NOW() AND c.terminate_at >= NOW() " +
                "ORDER BY c.create_at DESC " +
                "LIMIT 3";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return template.query(sql, params, (rs, rowNum) ->
                ProductDTO.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .price(rs.getInt("price"))
                        .image(rs.getString("image"))
                        .build()
        );
    }
}
