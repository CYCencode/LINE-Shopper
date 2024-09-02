package com.example.appbot.dao;

import com.example.appbot.dto.CampaignDTO;

import java.util.List;

public interface CampaignDao {
    Integer createCampaign(CampaignDTO dto);
    List<CampaignDTO> findCampaignList(CampaignDTO dto);
}
