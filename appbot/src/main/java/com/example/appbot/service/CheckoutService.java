package com.example.appbot.service;

import com.example.appbot.dto.CheckoutRequestDTO;

public interface CheckoutService {
    void handleCheckout(CheckoutRequestDTO dto);

    Boolean verifyPayment(CheckoutRequestDTO dto, Integer orderId, Integer totalPrice);

    Boolean verifyLogistic(CheckoutRequestDTO dto, Integer orderId, Integer totalPrice);
}
