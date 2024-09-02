package com.example.appbot.service;

import com.example.appbot.dto.LogisticDTO;
import com.example.appbot.dto.OrderDTO;
import com.example.appbot.dto.OrderDetailDTO;
import com.example.appbot.dto.PaymentDTO;

import java.util.List;

public interface OrderService {
    List<OrderDTO> findOrder(String orderNo, Integer page);
    Integer findCartByUserId(String lineUserId);
    void addToCart(String userId, String productId);
    List<OrderDetailDTO> findOrderDetailListByOrderId(Integer orderId);
    LogisticDTO findLogisticByOrderId(Integer orderId);
    PaymentDTO findPaymentByOrderId(Integer orderId);
}
