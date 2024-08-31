package com.example.appbot.service;

import com.example.appbot.dao.LogisticDao;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class LogisticServiceImpl implements LogisticService{

    private final LogisticDao logisticDao;

    public LogisticServiceImpl(LogisticDao logisticDao) {
        this.logisticDao = logisticDao;
    }

    @Override
    public Integer updateLogisticStatusByOrderNo(MultiValueMap<String, String> map) {
        return logisticDao.updateLogisticStatusByOrderNo(map);
    }
}
