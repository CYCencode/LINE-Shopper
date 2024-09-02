package com.example.appbot.service;

import com.example.appbot.dto.ProductDTO;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;

import java.util.List;


public interface LineBotService {
    Message handleTextMessage(MessageEvent<TextMessageContent> event);
    Message createProductButtonsTemplate(ProductDTO productDTO);
    Message createQuickReplyMessage();
    Message createCartQuickReplyMessage(String userId, String text);
    Message createTextMessage(String text);
    Message createCarouselMessage(List<ProductDTO> dtoList);
    String getUserProfile(String userId);
}
