package com.example.appbot.dao;

import com.example.appbot.dto.OrderDTO;
import com.example.appbot.enums.StatusCode;
import com.example.appbot.exception.CheckoutException;
import com.example.appbot.rowmapper.OrderDTORowMapper;
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
    private ZoneId getTimeZone() {
        return ZoneId.of("Asia/Taipei");
    }
    @Override
    public Integer findCartByUserId(String lineUserId){
        // 條件: order_status為cart
        String sql ="SELECT id FROM orders WHERE line_user_id = :user_id and order_status = :order_status";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", lineUserId)
            .addValue("order_status", StatusCode.ORDER_STATUS_CART.ordinal());
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
        ZonedDateTime currentTime = ZonedDateTime.now(getTimeZone());
        String sql = "INSERT INTO orders (line_user_id, order_status, total, create_at, last_modify_at) " +
                "VALUES (:line_user_id, :order_status, :total, :currentTime, :currentTime)";
        MapSqlParameterSource params = new MapSqlParameterSource();
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
    public Integer updateOrderStatus(Integer cartId, Integer orderStatus) {
        ZonedDateTime currentTime = ZonedDateTime.now(getTimeZone());
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder("UPDATE orders SET order_status = :status, last_modify_at = :lastModifyAt");
        params.addValue("status", orderStatus);
        params.addValue("id", cartId);
        params.addValue("lastModifyAt", currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (orderStatus == StatusCode.ORDER_STATUS_PAID.ordinal()) {
            String order_no = getTodaySerialNumber();
            params.addValue("order_no", order_no);
            sql.append(", order_no = :order_no");
        }

        sql.append(" WHERE id = :id");
        return template.update(sql.toString(), params);
    }
    /*
    TODO: consider database timezone vs. ZonedDateTime
     */

    @Override
    public String getTodaySerialNumber(){
        ZonedDateTime now = ZonedDateTime.now(getTimeZone());
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

    @Override
    public OrderDTO findOrderById(Integer id) {
        String sql = "select * from orders where id=:id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        try {
            return template.queryForObject(sql, params, new OrderDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new CheckoutException("訂單錯誤");
        }
    }

    @Override
    public Integer updateOrderNoById(Integer id, String orderNo) {
        String sql = "update orders set order_no = :order_no where id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id)
            .addValue("order_no", orderNo);

        return template.update(sql, params);
    }
}



