package com.example.appbot.dao;

import com.example.appbot.dto.LogisticDTO;
import com.example.appbot.exception.CheckoutException;
import com.example.appbot.rowmapper.LogisticDTORowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

@Repository
public class LogisticDaoImpl implements LogisticDao{

    private final NamedParameterJdbcTemplate template;

    public LogisticDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Integer createLogistic(LogisticDTO dto) {
        String sql = "INSERT INTO logistics (id, order_id, order_no, status, shipping, allpaylogistic_id, booking_note, receiver_address, receiver_phone, receiver_name, receiver_zipcode, receiver_email) " +
            "VALUES (default, :order_id, :order_no, :status, :shipping, :allpaylogistic_id, :booking_note, :receiver_address, :receiver_phone, :receiver_name, :receiver_zipcode, :receiver_email)";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("order_id", dto.getOrderId())
            .addValue("order_no", dto.getOrderNo())
            .addValue("status", dto.getStatus())
            .addValue("shipping", dto.getShipping())
            .addValue("allpaylogistic_id", dto.getAllPayLogisticId())
            .addValue("booking_note", dto.getBookingNote())
            .addValue("receiver_address", dto.getReceiverAddress())
            .addValue("receiver_phone", dto.getReceiverCellPhone())
            .addValue("receiver_name", dto.getReceiverName())
            .addValue("receiver_zipcode", dto.getReceiverZipcode())
            .addValue("receiver_email", dto.getReceiverEmail());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(sql, param, keyHolder, new String[]{"id"});

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().intValue();
        } else {
            throw new RuntimeException("logistic");
        }
    }
    @Override
    public LogisticDTO searchLogisticByOrderNo(String orderNo) {
        String sql = "SELECT id, order_id, order_no, status, shipping, allpaylogistic_id, booking_note, receiver_address, receiver_phone, receiver_name, receiver_zipcode, receiver_email " +
                "FROM logistics WHERE order_no = :orderNo";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderNo", orderNo);

        try {
            return template.queryForObject(sql, params, new LogisticDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new CheckoutException("找不到訂單編號為 " + orderNo + " 的物流資訊。");
        }
    }

    @Override
    public LogisticDTO findLogisticByOrderId(Integer orderId) {
        String sql = "SELECT id, order_id, order_no, status, shipping, allpaylogistic_id, booking_note, receiver_address, receiver_phone, receiver_name, receiver_zipcode, receiver_email " +
            "FROM logistics WHERE order_id = :order_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("order_id", orderId);

        try {
            return template.queryForObject(sql, params, new LogisticDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer updateLogisticStatusByOrderNo(MultiValueMap<String, String> map) {
        String sql = "UPDATE logistics SET status = :status WHERE order_no = :order_no";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", map.get("RtnCode"))
            .addValue("order_no", map.get("MerchantTradeNo"));
        return template.update(sql, params);
    }
}
