package com.example.appbot.dao;

import com.example.appbot.dto.ProductDTO;
import com.example.appbot.rowmapper.ProductDTORowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Override
    public List<ProductDTO> findAllProduct() {
        String sql = "SELECT * FROM products";
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return template.query(sql, params, new ProductDTORowMapper());
        } catch (Exception e) {
            return List.of();
        }
    }

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
        String sql = """
                SELECT c.id, p.id, p.name, (p.price * c.discount_rate) AS price, p.image
                FROM products p
                JOIN campaigns c ON c.product_id = p.id
                JOIN (
                    SELECT product_id, MAX(id) AS max_id
                    FROM campaigns
                    WHERE create_at <= NOW() AND terminate_at >= NOW()
                    GROUP BY product_id
                ) latest_campaign ON latest_campaign.product_id = c.product_id AND latest_campaign.max_id = c.id
                ORDER BY c.create_at DESC, c.id DESC LIMIT 3;
                """;
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
    @Override
    public Integer createProduct(ProductDTO productDTO){
        String sql = "INSERT INTO products (price, stock, category, name, image) " +
                "VALUES (:price, :stock, :category, :name, :image)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("price", productDTO.getPrice());
        params.addValue("stock", productDTO.getStock());
        params.addValue("category", productDTO.getCategory());
        params.addValue("name", productDTO.getName());
        params.addValue("image", productDTO.getImage());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public Integer updateStockByOrderId(Integer orderId) {
        String sql = "UPDATE products p " +
            "INNER JOIN order_details od ON p.id = od.product_id " +
            "SET p.stock = p.stock - od.quantity " +
            "WHERE p.stock >= 0 AND p.stock >= od.quantity AND od.order_id = :order_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("order_id", orderId);

        return template.update(sql, params);
    }
}
