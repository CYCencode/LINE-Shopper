package com.example.appbot.dao;

import com.example.appbot.dto.CampaignDTO;
import com.example.appbot.rowmapper.CampaignDTORowMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CampaignDaoImpl implements CampaignDao{

    private final NamedParameterJdbcTemplate template;

    public CampaignDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Integer createCampaign(CampaignDTO dto) {
        String sql = "INSERT INTO campaigns (id, product_id, campaign_name, create_at, terminate_at, discount_rate) VALUES " +
            "(DEFAULT, :product_id, :campaign_name, :create_at, :terminate_at, :discount_rate)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("product_id", dto.getProductId())
            .addValue("campaign_name", dto.getName())
            .addValue("create_at", dto.getCreateAt())
            .addValue("terminate_at", dto.getTerminateAt())
            .addValue("discount_rate", dto.getDiscountRate());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(sql, params, keyHolder, new String[]{"id"});

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().intValue();
        } else {
            throw new RuntimeException("campaign");
        }
    }

    @Override
    public List<CampaignDTO> findCampaignList(CampaignDTO dto) {
        StringBuilder sb = new StringBuilder("SELECT * FROM campaigns WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (dto.getId() != null) {
            sb.append("AND id=:id ");
            params.addValue("id", dto.getId());
        }
        if (Strings.isNotBlank(dto.getName())) {
            sb.append("AND campaign_name LIKE :campaign_name ");
            params.addValue("campaign_name",  "%" + dto.getName() + "%");
        }
        if (Strings.isNotBlank(dto.getCreateAt())) {
            sb.append("AND create_at <= :create_at ");
            params.addValue("create_at",  dto.getCreateAt());
        }
        if (Strings.isNotBlank(dto.getTerminateAt())) {
            sb.append("AND terminate_at >= :terminate_at ");
            params.addValue("terminate_at",  dto.getTerminateAt());
        }
        if (dto.getDiscountRate() != null) {
            sb.append("AND discount_rate = :discount_rate ");
            params.addValue("discount_rate",  dto.getDiscountRate());
        }


        try {
            return template.query(sb.toString(), params, new CampaignDTORowMapper());
        } catch (Exception e) {
            return List.of();
        }
    }
}
