package com.example.appbot.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class OrderDaoImpl implements OrderDao{
    @Value("${ORDER_PREFIX}")
    private String ORDER_PREFIX;
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
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        String order_no = getTodaySerialNumber();
        String sql = "INSERT INTO orders (order_no, line_user_id, order_status, total, create_at, last_modify_at) " +
                "VALUES (:order_no, :line_user_id, :order_status, :total, :currentTime, :currentTime)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("order_no", order_no);
        params.addValue("line_user_id", lineUserId);
        params.addValue("order_status", orderStatus);
        params.addValue("total", price);
        params.addValue("currentTime", currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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

    @Override
    public Integer updateOrderStatus(Integer cartId, Integer orderStatus){
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        String sql = "UPDATE orders SET order_status = :status, last_modify_at = :lastModifyAt WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", orderStatus);
        params.addValue("id", cartId);
        params.addValue("lastModifyAt", currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return template.update(sql, params);
    }

    @Override
    public String getTodaySerialNumber(){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        String date = now.format(DateTimeFormatter.ofPattern("yyMMdd"));
        char hour = (char)('A'+now.getHour());
        String sql ="SELECT COUNT(*) FROM orders WHERE DATE(create_at) = :today AND HOUR(create_at) = :hour";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("today", now.toLocalDate());
        params.addValue("hour", now.getHour());

        Integer count = template.queryForObject(sql, params, Integer.class);
        String serialNumber = String.format("%06d", count+1);
        return ORDER_PREFIX+date+hour+serialNumber;

    }

}



