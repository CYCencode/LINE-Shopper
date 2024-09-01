package com.example.appbot.handler;

import com.example.appbot.exception.CheckoutException;
import com.example.appbot.service.LineBotService;
import com.example.appbot.service.OrderService;
import com.example.appbot.util.PostbackDataParser;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.appbot.enums.StatusCode;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@LineMessageHandler
public class LineBotMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(LineBotMessageHandler.class);
    private final LineBotService lineBotService;
    private final OrderService orderService;
    public LineBotMessageHandler(LineBotService lineBotService,OrderService orderService){
        this.lineBotService = lineBotService;
        this.orderService = orderService;
    }
    @EventMapping
    public Message handleMessageEvent(MessageEvent<TextMessageContent> event){
        logger.info("Handling message event: {}", event);
        try {
            return lineBotService.handleTextMessage(event);
        } catch (Exception e) {
            e.printStackTrace();
            return new TextMessage("處理訊息錯誤，請稍後嘗試");
        }
    }
    @EventMapping
    public Message handlePostbackEvent(PostbackEvent postbackEvent) {
        String userId = postbackEvent.getSource().getUserId();
        String data = postbackEvent.getPostbackContent().getData();
        Map<String, String> postbackData = PostbackDataParser.parse(data);

        String actionValue = postbackData.get("action");
        String productId = postbackData.get("product_id");
        String productName = postbackData.get("product_name");

        if ("add_to_cart".equals(actionValue)) {
            try {
                orderService.addToCart(userId, productId);
                return lineBotService.createCartQuickReplyMessage(userId, productName + " 已加入購物車");
            } catch (CheckoutException e) {
                return lineBotService.createCartQuickReplyMessage(userId, productName + " " + e.getMessage());
            } catch (Exception e) {
                return new TextMessage("系統忙碌中，請稍後");
            }
        }
        return null;
    }
    @EventMapping
    public Message handleDefaultMessageEvent(Event event) {
        logger.info("Received non-text message event: {}", event);
        return lineBotService.createTextMessage("目前僅支援文字對話功能呦～");
    }
}



