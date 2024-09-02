package com.example.appbot.dao;

import com.example.appbot.dto.PaymentDTO;
import com.example.appbot.dto.TappayResultDTO;
import com.example.appbot.rowmapper.LogisticDTORowMapper;
import com.example.appbot.rowmapper.PaymentDTORowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

@Repository
public class PaymentDaoImpl implements PaymentDao{

    private final NamedParameterJdbcTemplate template;

    public PaymentDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Integer createPayment(Integer orderId, String method, TappayResultDTO trDTO) {
        String sql = "INSERT INTO payments (id, order_id, method, rec_trade_id, bank_transaction_id, bank_order_number, auth_code, amount, currency, transaction_time_millis, bank_result_code, bank_result_msg, card_identifier) VALUES " +
            "(DEFAULT, :order_id, :method, :rec_trade_id, :bank_transaction_id, :bank_order_number, :auth_code, :amount, :currency, :transaction_time_millis, :bank_result_code, :bank_result_msg, :card_identifier)";
        MapSqlParameterSource param = new MapSqlParameterSource();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(trDTO.getTransactionTimeMillis());

        param.addValue("order_id", orderId)
            .addValue("method", method)
            .addValue("rec_trade_id", trDTO.getRecTradeId())
            .addValue("bank_transaction_id", trDTO.getBankTransactionId())
            .addValue("bank_order_number", trDTO.getBankOrderNumber())
            .addValue("auth_code", trDTO.getAuthCode())
            .addValue("amount", trDTO.getAmount())
            .addValue("currency", trDTO.getCurrency())
            .addValue("transaction_time_millis", calendar.getTime())
            .addValue("bank_result_code", trDTO.getBankResultCode())
            .addValue("bank_result_msg", trDTO.getBankResultMsg())
            .addValue("card_identifier", trDTO.getCardIdentifier());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(sql, param, keyHolder, new String[]{"id"});

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().intValue();
        } else {
            throw new RuntimeException("payment");
        }
    }

    @Override
    public PaymentDTO findPaymentByOrderId(Integer orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = :order_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("order_id", orderId);

        try {
            return template.queryForObject(sql, params, new PaymentDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
