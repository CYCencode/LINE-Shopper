package com.example.appbot.dao;

public interface OrderDetailDao {
    Integer addOrderDetail(Integer cartId, Integer productId, Integer quantity);
    Integer calcCartTotal(Integer cartId);
}