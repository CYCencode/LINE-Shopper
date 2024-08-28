package com.example.appbot.handler;

import com.example.appbot.service.LineBotService;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@LineMessageHandler
public class LineBotMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(LineBotMessageHandler.class);
    private final LineBotService lineBotService;
    private final LineMessagingClient lineMessagingClient;
    public LineBotMessageHandler(LineBotService lineBotService, LineMessagingClient lineMessagingClient){
        this.lineBotService = lineBotService;
        this.lineMessagingClient = lineMessagingClient;
    }
    @EventMapping
    public void handleMessageEvent(MessageEvent<TextMessageContent> event){
        Message message = lineBotService.handleTextMessage(event);
        String replyToken = event.getReplyToken();
        lineMessagingClient.replyMessage(new ReplyMessage(replyToken, message))
                .whenComplete((response, throwable) -> {
                    if (throwable != null) {
                        logger.error("Error occurred while sending message: " + throwable.getMessage());
                    } else {
                        logger.info("Message sent successfully!");
                    }
                });
    }
}
