package com.example.appbot.service;

import com.example.appbot.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    List<OrderDTO> findOrder(String orderNo, Integer page);
    void addToCart(String userId, String productId);
}
