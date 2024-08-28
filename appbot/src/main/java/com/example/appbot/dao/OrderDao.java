package com.example.appbot.dao;

import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public interface OrderDao {
    Integer createOrder(String lineUserId, Integer orderStatus, Integer productId);
    Integer findCartByUserId(String lineUserId);
    Integer updateOrderTotal(Integer cartId);
}