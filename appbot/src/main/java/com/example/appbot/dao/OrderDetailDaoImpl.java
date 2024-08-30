package com.example.appbot.dao;

import com.example.appbot.dto.OrderDetailDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDetailDaoImpl implements OrderDetailDao {
    private final NamedParameterJdbcTemplate template;
    public OrderDetailDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Integer findCountOrderDetailByOrderId(Integer cartId, Integer productId) {
        String sql = "SELECT count(*) num FROM order_details WHERE order_id = :order_id AND product_id = :product_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("order_id", cartId)
            .addValue("product_id", productId);

        return template.queryForObject(sql, params, Integer.class);
    }

    @Override
    public Integer incQtyOrderDetailByOrderId(Integer cartId, Integer productId) {
        String sql = "UPDATE order_details SET quantity = quantity + 1 WHERE order_id = :order_id and product_id = :product_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("product_id", productId)
            .addValue("order_id", cartId);

        return template.update(sql, params);
    }
    @Override
    public Integer addOrderDetail(Integer cartId, Integer productId, Integer quantity){
        String sql = "INSERT INTO order_details (quantity, order_id, product_id) " +
                "VALUES (:quantity, :orderId , :productId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("quantity", quantity);
        params.addValue("productId", productId);
        params.addValue("orderId", cartId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, params, keyHolder, new String[] {"id"});
        Integer orderDetailId = keyHolder.getKey().intValue();
        return orderDetailId;
    };

    public void updateCampaignIdForCart(Integer cartId) {
        String sql = "UPDATE order_details od " +
                "JOIN campaigns c ON od.product_id = c.product_id " +
                "AND c.create_at <= NOW() AND c.terminate_at >= NOW() " +
                "SET od.campaign_id = c.id " +
                "WHERE od.order_id = :cartId";

        MapSqlParameterSource params = new MapSqlParameterSource("cartId", cartId);

        template.update(sql, params);
    }

    /*
    till calc total price, consider campaign, add campaign info
     */
    @Override
    public List<OrderDetailDTO> calcCartTotal(Integer cartId) {
        // 先更新 order_details 表中的 campaign_id
        updateCampaignIdForCart(cartId);
        // 再計算原價和折扣價，return OrderDetailDTO
        String sql = "SELECT od.id, od.product_id, od.campaign_id, od.quantity, " +
                "p.price AS originalPrice, " +
                "CASE WHEN od.campaign_id IS NOT NULL THEN p.price * c.discount_rate ELSE p.price END AS discountedPrice " +
                "FROM order_details od " +
                "JOIN products p ON od.product_id = p.id " +
                "LEFT JOIN campaigns c ON od.campaign_id = c.id " +
                "WHERE od.order_id = :cartId";

        MapSqlParameterSource params = new MapSqlParameterSource("cartId", cartId);

        return template.query(sql, params, (rs, rowNum) ->
                OrderDetailDTO.builder()
                        .id(rs.getInt("id"))
                        .orderId(cartId)
                        .campaignId(rs.getInt("campaign_id"))
                        .productId(rs.getInt("product_id"))
                        .quantity(rs.getInt("quantity"))
                        .originalPrice(rs.getInt("originalPrice")) // 自動取整
                        .discountedPrice(rs.getInt("discountedPrice"))
                        .build()
        );
    }

    @Override
    public List<OrderDetailDTO> findOrderDetailListByOrderId(Integer orderId) {
        String sql = "SELECT od.id, od.product_id, od.order_id, od.campaign_id, od.quantity, p.name AS productName, " +
            "p.price AS originalPrice, " +
            "CASE WHEN od.campaign_id IS NOT NULL THEN p.price * c.discount_rate ELSE p.price END AS discountedPrice " +
            "FROM order_details od " +
            "JOIN products p ON od.product_id = p.id " +
            "LEFT JOIN campaigns c ON od.campaign_id = c.id " +
            "WHERE od.order_id = :cartId";

        MapSqlParameterSource params = new MapSqlParameterSource("cartId", orderId);

        try {
            return template.query(sql, params, (rs, rowNum) ->
                OrderDetailDTO.builder()
                    .id(rs.getInt("id"))
                    .orderId(rs.getInt("order_id"))
                    .campaignId(rs.getInt("campaign_id"))
                    .productId(rs.getInt("product_id"))
                    .productName(rs.getString("productName"))
                    .quantity(rs.getInt("quantity"))
                    .originalPrice(rs.getInt("originalPrice")) // 自動取整
                    .discountedPrice(rs.getInt("discountedPrice"))
                    .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
