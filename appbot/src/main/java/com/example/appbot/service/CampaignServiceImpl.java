package com.example.appbot.service;

import com.example.appbot.dao.CampaignDao;
import com.example.appbot.dto.CampaignDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService{

    private final CampaignDao campaignDao;

    public CampaignServiceImpl(CampaignDao campaignDao) {
        this.campaignDao = campaignDao;
    }

    @Override
    public List<CampaignDTO> findCampaignList(CampaignDTO dto) {
        return campaignDao.findCampaignList(dto);
    }

    @Override
    public Integer createCampaign(CampaignDTO dto) {
        return campaignDao.createCampaign(dto);
    }
}
