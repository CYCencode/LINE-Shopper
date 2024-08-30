package com.example.appbot.dao;

import com.example.appbot.dto.TappayResultDTO;

public interface PaymentDao {
    Integer createPayment(Integer orderId, String method, TappayResultDTO trDTO);
}
