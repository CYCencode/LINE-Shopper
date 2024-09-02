package com.example.appbot.rowmapper;

import com.example.appbot.dto.PaymentDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentDTORowMapper implements RowMapper<PaymentDTO> {
    @Override
    public PaymentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PaymentDTO.builder()
            .id(rs.getInt("id"))
            .method(rs.getString("method"))
            .recTradeId(rs.getString("rec_trade_id"))
            .bankTransactionId(rs.getString("bank_transaction_id"))
            .bankOrderNumber(rs.getString("bank_order_number"))
            .authCode(rs.getString("auth_code"))
            .amount(rs.getInt("amount"))
            .currency(rs.getString("currency"))
            .transactionTime(rs.getString("transaction_time_millis"))
            .bankResultCode(rs.getString("bank_result_code"))
            .bankResultMsg(rs.getString("bank_result_code"))
            .cardIdentifier(rs.getString("card_identifier"))
            .build();
    }
}
