package com.example.appbot.dao;

import com.example.appbot.dto.OrderDTO;
import com.example.appbot.dto.OrderDetailDTO;
import com.example.appbot.enums.StatusCode;
import com.example.appbot.exception.CheckoutException;
import com.example.appbot.rowmapper.OrderDTORowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class OrderDaoImpl implements OrderDao{
    @Value("${ORDER_PREFIX}")
    private String ORDER_PREFIX;
    private final NamedParameterJdbcTemplate template;
    private final ProductDao productDao;
    private final OrderDetailDao orderDetailDao;
    private static final Logger logger = LoggerFactory.getLogger(OrderDaoImpl.class);
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
    public Integer createOrder(String lineUserId, String lineUserName, Integer orderStatus){
        ZonedDateTime currentTime = ZonedDateTime.now(getTimeZone());
        String order_no = getTodaySerialNumber();
        String sql = "INSERT INTO orders (line_user_id, order_no, line_user_name ,order_status, total, create_at, last_modify_at) " +
                "VALUES (:line_user_id, :order_no ,:line_user_name , :order_status, :total, :currentTime, :currentTime)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("line_user_id", lineUserId);
        params.addValue("order_no", order_no);
        params.addValue("line_user_name", lineUserName);
        params.addValue("order_status", orderStatus);
        params.addValue("total", 0);
        params.addValue("currentTime", currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, params, keyHolder, new String[] {"id"});
        Integer orderId = keyHolder.getKey().intValue();
        return orderId;
    }

    @Override
    public List<OrderDetailDTO> updateOrderTotal(Integer cartId) {
        List<OrderDetailDTO> orderDetails = orderDetailDao.calcCartTotal(cartId);
        Integer total = orderDetails.stream()
                .mapToInt(detail -> detail.getDiscountedPrice() * detail.getQuantity())
                .sum();
        logger.info(String.format("Total : %d", total));
        String sql = "UPDATE orders SET total = :total WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", cartId);
        params.addValue("total", total);
        template.update(sql, params);

        return orderDetails;
    }


    @Override
    public Integer updateOrderStatus(Integer cartId, Integer orderStatus) {
        ZonedDateTime currentTime = ZonedDateTime.now(getTimeZone());
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = "UPDATE orders SET order_status = :status, last_modify_at = :lastModifyAt WHERE id = :id";
        params.addValue("status", orderStatus);
        params.addValue("id", cartId);
        params.addValue("lastModifyAt", currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return template.update(sql, params);
    }

    @Override
    public String getTodaySerialNumber(){
        ZonedDateTime now = ZonedDateTime.now(getTimeZone());
        String date = now.format(DateTimeFormatter.ofPattern("yyMMdd"));
        char hour = (char)('A'+now.getHour());
        String sql ="SELECT COUNT(*) FROM orders WHERE DATE(create_at) = :today AND order_status = :order_status";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("today", now.toLocalDate())
            .addValue("order_status", StatusCode.ORDER_STATUS_PAID.ordinal());

        Integer count = template.queryForObject(sql, params, Integer.class);
        String serialNumber = String.format("%06d", count+1);
        return ORDER_PREFIX+date+hour+serialNumber;

    }

    @Override
    public OrderDTO findOrderById(Integer id) {
        String sql = "SELECT * FROM orders WHERE id=:id";
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
        String sql = "UPDATE orders SET order_no = :order_no WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id)
            .addValue("order_no", orderNo);

        return template.update(sql, params);
    }

    @Override
    public List<OrderDTO> findOrder(String orderNo, Integer page) {
        StringBuilder sb = new StringBuilder("SELECT * FROM orders WHERE 1=1 ");
        if(orderNo != null) {
            sb.append("AND order_no LIKE :order_no ORDER BY order_no ");
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("order_no", "%" +orderNo+"%");

        return template.query(sb.toString(), params, new OrderDTORowMapper());
    }
    @Override
    public Integer findOrderIdByOrderNo(String orderNo){
        String sql = "SELECT id FROM orders WHERE order_no = :order_no";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("order_no", orderNo);

        try {
            return template.queryForObject(sql, params, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}



