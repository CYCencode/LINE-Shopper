package com.example.appbot.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDaoImpl implements OrderDao{
    private final NamedParameterJdbcTemplate template;
    private final ProductDao productDao;
    private final OrderDetailDao orderDetailDao;
    public OrderDaoImpl(NamedParameterJdbcTemplate template, ProductDao productDao, OrderDetailDao orderDetailDao) {
        this.template = template;
        this.productDao = productDao;
        this.orderDetailDao = orderDetailDao;
    }
    @Override
    public Integer findCartByUserId(String lineUserId){
        String sql ="SELECT id FROM orders WHERE line_user_id = :user_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", lineUserId);
        try {
            return template.queryForObject(sql, params, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    @Override
    public Integer createOrder(String lineUserId, Integer orderStatus, Integer productId){
        /*TODO: since we only add one order once a time,
           future need to init total as 0, calculate total & update total
         */
        Integer price = productDao.findProductPrice(productId);
        String sql = "INSERT INTO orders (line_user_id, order_status, total, create_at, last_modify_at) " +
                "VALUES (:line_user_id, :order_status, :total, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("line_user_id", lineUserId);
        params.addValue("order_status", orderStatus);
        params.addValue("total", price);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, params, keyHolder, new String[] {"id"});
        Integer orderId = keyHolder.getKey().intValue();
        return orderId;
    }

    @Override
    public Integer updateOrderTotal(Integer cartId){
        Integer total = orderDetailDao.calcCartTotal(cartId);
        String sql = "UPDATE orders SET total = :total WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", cartId);
        params.addValue("total", total);
        return template.update(sql, params);
    }

}



