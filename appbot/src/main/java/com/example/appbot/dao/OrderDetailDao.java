package com.example.appbot.dao;

import com.example.appbot.dto.OrderDetailDTO;

import java.util.List;

public interface OrderDetailDao {
    Integer findCountOrderDetailByOrderId(Integer cartId, Integer productId);
    Integer incQtyOrderDetailByOrderId(Integer cartId, Integer productId);
    Integer addOrderDetail(Integer cartId, Integer productId, Integer quantity);
    List<OrderDetailDTO> calcCartTotal(Integer cartId);
    List<OrderDetailDTO> findOrderDetailListByOrderId(Integer orderId);
    void updateCampaignIdForCart(Integer cartId);
    Integer getOrderDetailCountByOrderId(Integer orderId);
    Integer getOrderDetailInsufficientCountByOrderId(Integer orderId);
}