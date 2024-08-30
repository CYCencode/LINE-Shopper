package com.example.appbot.handler;

import com.example.appbot.service.LineBotService;
import com.example.appbot.service.OrderService;
import com.example.appbot.util.PostbackDataParser;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
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
        return lineBotService.handleTextMessage(event);

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
            orderService.addToCart(userId, productId);
            return lineBotService.createCartQuickReplyMessage(userId, productName + " 已加入購物車");
        }
        return null;
    }

}



