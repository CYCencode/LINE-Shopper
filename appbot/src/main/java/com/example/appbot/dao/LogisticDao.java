package com.example.appbot.dao;

import com.example.appbot.dto.LogisticDTO;
import org.springframework.util.MultiValueMap;

public interface LogisticDao {
    Integer createLogistic(LogisticDTO dto);
    LogisticDTO searchLogisticByOrderNo(String orderNo);
    LogisticDTO findLogisticByOrderId(Integer orderId);
    Integer updateLogisticStatusByOrderNo(MultiValueMap<String, String> map);
}
