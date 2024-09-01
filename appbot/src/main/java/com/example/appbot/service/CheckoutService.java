package com.example.appbot.service;

import com.example.appbot.dto.CheckoutRequestDTO;
import com.example.appbot.dto.OrderDTO;

public interface CheckoutService {
    void handleCheckout(CheckoutRequestDTO dto);

    void verifyProductStock(Integer orderId);

    Boolean verifyPayment(CheckoutRequestDTO crDTO, OrderDTO orderDTO);

    Boolean verifyLogistic(CheckoutRequestDTO crDTO, OrderDTO orderDTO);

    void replyUser(String lineUserId, String msg);
}
