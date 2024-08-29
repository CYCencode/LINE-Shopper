package com.example.appbot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDaoImpl implements PaymentDao{

    private final NamedParameterJdbcTemplate template;

    public PaymentDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Integer createPayment(Integer orderId, String method) {
        String sql = "INSERT INTO payments (id, order_id, method) VALUES (default, :order_id, :method)";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("order_id", orderId)
            .addValue("method", method);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(sql, param, keyHolder, new String[]{"id"});

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().intValue();
        } else {
            throw new RuntimeException("payment");
        }
    }
}
