package com.example.appbot.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDetailDaoImpl implements OrderDetailDao {
    private final NamedParameterJdbcTemplate template;
    public OrderDetailDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Integer findCountOrderDetailByOrderId(Integer cartId, Integer productId) {
        String sql = "SELECT count(*) num FROM order_details WHERE order_id = :order_id AND product_id = :product_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("order_id", cartId)
            .addValue("product_id", productId);

        return template.queryForObject(sql, params, Integer.class);
    }

    @Override
    public Integer incQtyOrderDetailByOrderId(Integer cartId, Integer productId) {
        String sql = "UPDATE order_details SET quantity = quantity + 1 WHERE order_id = :order_id and product_id = :product_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("product_id", productId)
            .addValue("order_id", cartId);

        return template.update(sql, params);
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
