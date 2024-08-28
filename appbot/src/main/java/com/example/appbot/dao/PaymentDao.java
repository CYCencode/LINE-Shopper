package com.example.appbot.dao;

public interface PaymentDao {
    Integer createPayment(Integer orderId, String method);
}
