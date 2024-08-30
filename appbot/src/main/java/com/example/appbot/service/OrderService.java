package com.example.appbot.service;

import com.example.appbot.dto.OrderDTO;
import com.example.appbot.dto.OrderDetailDTO;

import java.util.List;

public interface OrderService {
    List<OrderDTO> findOrder(String orderNo, Integer page);
    Integer findCartByUserId(String lineUserId);
    Integer findOrderByUserId(String lineUserId);
    void addToCart(String userId, String productId);
    List<OrderDetailDTO> findOrderDetailListByOrderId(Integer orderId);
}
