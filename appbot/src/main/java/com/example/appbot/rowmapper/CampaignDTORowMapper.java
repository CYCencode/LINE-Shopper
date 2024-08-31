package com.example.appbot.rowmapper;

import com.example.appbot.dto.CampaignDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CampaignDTORowMapper implements RowMapper<CampaignDTO> {
    @Override
    public CampaignDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CampaignDTO.builder()
            .id(rs.getInt("id"))
            .productId(rs.getInt("product_id"))
            .name(rs.getString("campaign_name"))
            .createAt(rs.getString("create_at"))
            .terminateAt(rs.getString("terminate_at"))
            .discountRate(rs.getFloat("discount_rate"))
            .build();
    }
}
