package com.example.appbot.service;

import com.example.appbot.dto.CampaignDTO;

import java.util.List;

public interface CampaignService {
    Integer createCampaign(CampaignDTO dto);
    List<CampaignDTO> findCampaignList(CampaignDTO dto);
}
