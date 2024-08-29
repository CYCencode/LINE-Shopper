package com.example.appbot.rowmapper;

import com.example.appbot.dao.OrderDao;
import com.example.appbot.dto.OrderDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class OrderDTORowMapper implements RowMapper<OrderDTO> {

    private static ZoneId zoneId = ZoneId.of("Asia/Taipei");

    @Override
    public OrderDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createAt = rs.getTimestamp("create_at");
        ZonedDateTime zCreateAt = createAt == null ? null : createAt.toInstant().atZone(zoneId);
        Timestamp lastModifyAt = rs.getTimestamp("last_modify_at");
        ZonedDateTime zLastModifyAt = lastModifyAt == null ? null : lastModifyAt.toInstant().atZone(zoneId);
        return OrderDTO.builder()
            .id(rs.getInt("id"))
//            .orderNo(rs.getString("order_no"))
            .lineUserId(rs.getString("line_user_id"))
            .orderStatus(rs.getInt("order_status"))
            .total(rs.getInt("total"))
            .createAt(zCreateAt)
            .lastModifiedAt(zLastModifyAt)
            .build();
    }
}
