package com.example.appbot.dao;

public interface OrderDetail {
    Integer addOrderDetail(Integer cartId, Integer productId, Integer quantity);
    Integer calcCartTotal(Integer cartId);
}