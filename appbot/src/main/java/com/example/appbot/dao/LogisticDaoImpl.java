package com.example.appbot.dao;

import com.example.appbot.dto.LogisticDTO;
import com.example.appbot.exception.CheckoutException;
import com.example.appbot.rowmapper.LogisticDTORowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class LogisticDaoImpl implements LogisticDao{

    private final NamedParameterJdbcTemplate template;

    public LogisticDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Integer createLogistic(LogisticDTO dto) {
        String sql = "INSERT INTO logistics (id, order_id, order_no, status, shipping, allpaylogistic_id, booking_note) VALUES (default, :order_id, :order_no, :status, :shipping, :allpaylogistic_id, :booking_note)";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("order_id", dto.getOrderId())
            .addValue("order_no", dto.getOrderNo())
            .addValue("status", dto.getStatus())
            .addValue("shipping", dto.getShipping())
            .addValue("allpaylogistic_id", dto.getAllPayLogisticId())
            .addValue("booking_note", dto.getBookingNote());
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
        String sql = "SELECT id, order_id, order_no, status, shipping, allpaylogistic_id, booking_note " +
                "FROM logistics WHERE order_no = :orderNo";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderNo", orderNo);

        try {
            return template.queryForObject(sql, params, new LogisticDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new CheckoutException("找不到訂單編號為 " + orderNo + " 的物流資訊。");
        }
    }


}
