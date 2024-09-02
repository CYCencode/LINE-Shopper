package com.example.appbot.service;

import org.springframework.util.MultiValueMap;

public interface LogisticService {
    Integer updateLogisticStatusByOrderNo(MultiValueMap<String, String> map);
}
