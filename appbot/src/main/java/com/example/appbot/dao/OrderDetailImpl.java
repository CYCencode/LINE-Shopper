package com.example.appbot.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDetailImpl implements OrderDetail {
    private final NamedParameterJdbcTemplate template;
    public OrderDetailImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }
    @Override
    public Integer addOrderDetail(Integer cartId, Integer productId, Integer quantity){
        String sql = "INSERT INTO order_details (quantity, order_id, product_id) " +
                "VALUES (:quantity, :orderId , :productId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("quantity", quantity);
        params.addValue("productId", productId);
        params.addValue("orderId", cartId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, params, keyHolder, new String[] {"id"});
        Integer orderDetailId = keyHolder.getKey().intValue();
        return orderDetailId;
    };

    @Override
    public Integer calcCartTotal(Integer cartId){
        String sql ="SELECT SUM(p.price) AS total " +
                "FROM order_details od " +
                "JOIN products p ON od.product_id = p.id " +
                "WHERE od.order_id = :cartId";
        MapSqlParameterSource params = new MapSqlParameterSource("cartId", cartId);
        params.addValue("cartId", cartId);
        return template.queryForObject(sql, params, Integer.class);
    };

}
