package com.example.appbot.service;

import com.example.appbot.dto.CheckoutRequestDTO;

public interface CheckoutService {
    void handleCheckout(CheckoutRequestDTO dto);

    Boolean verifyPayment(CheckoutRequestDTO dto);

    Boolean verifyLogistic(CheckoutRequestDTO dto);
}
