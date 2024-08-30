package com.example.appbot.rowmapper;

import com.example.appbot.dto.LogisticDTO;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogisticDTORowMapper implements RowMapper<LogisticDTO> {
    @Override
    public LogisticDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return LogisticDTO.builder()
                .id(rs.getInt("id"))
                .orderId(rs.getInt("order_id"))
                .orderNo(rs.getString("order_no"))
                .status(rs.getString("status"))
                .shipping(rs.getString("shipping"))
                .allPayLogisticId(rs.getString("allpaylogistic_id"))
                .bookingNote(rs.getString("booking_note"))
                .build();
    }
}
