package com.example.appbot.dao;

public interface OrderDetailDao {
    Integer findCountOrderDetailByOrderId(Integer cartId, Integer productId);
    Integer incQtyOrderDetailByOrderId(Integer cartId, Integer productId);
    Integer addOrderDetail(Integer cartId, Integer productId, Integer quantity);
    Integer calcCartTotal(Integer cartId);
}