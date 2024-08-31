package com.example.appbot.dao;

import com.example.appbot.dto.LogisticDTO;

public interface LogisticDao {
    Integer createLogistic(LogisticDTO dto);
    LogisticDTO searchLogisticByOrderNo(String orderNo);
    LogisticDTO findLogisticByOrderId(Integer orderId);
}
