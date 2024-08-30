package com.example.appbot.dao;

import com.example.appbot.dto.OrderDTO;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

public interface OrderDao {
    Integer createOrder(String lineUserId, Integer orderStatus, Integer productId);
    Integer findCartByUserId(String lineUserId);
    Integer updateOrderTotal(Integer cartId);
    Integer updateOrderStatus(Integer cartId,Integer orderStatus);
    String getTodaySerialNumber();
    OrderDTO findOrderById(Integer id);
    Integer updateOrderNoById(Integer id, String orderNo);

    List<OrderDTO> findOrder(String orderNo, Integer page);
}