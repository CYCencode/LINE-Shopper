package com.example.appbot.handler;

import com.example.appbot.dao.OrderDao;
import com.example.appbot.dao.OrderDetailDao;
import com.example.appbot.enums.LimitAmount;
import com.example.appbot.service.LineBotService;
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

@Component
@LineMessageHandler
public class LineBotMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(LineBotMessageHandler.class);
    private final LineBotService lineBotService;
    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;
    public LineBotMessageHandler(LineBotService lineBotService, OrderDao orderDao, OrderDetailDao orderDetailDao){
        this.lineBotService = lineBotService;
        this.orderDao = orderDao;
        this.orderDetailDao = orderDetailDao;
    }
    @EventMapping
    public Message handleMessageEvent(MessageEvent<TextMessageContent> event){
        return lineBotService.handleTextMessage(event);

    }
    @EventMapping
    public Message handlePostbackEvent(PostbackEvent postbackEvent) {
        String userId = postbackEvent.getSource().getUserId();
        String data = postbackEvent.getPostbackContent().getData();

        // 解析 action 的值
        String action = data.substring(0, data.indexOf("&"));
        String actionValue = action.split("=")[1];
        // TODO: don't repeat your self
        // 解析 productId
        String productId = data.substring(data.indexOf("product_id=") + "product_id=".length());
        if (productId.contains("&")) {
            productId = productId.substring(0, productId.indexOf("&"));
        }

        // 解析 productName
        String productName = data.substring(data.indexOf("product_name=") + "product_name=".length());
        if (productName.contains("&")) {
            productName = productName.substring(0, productName.indexOf("&"));
        }

        // TODO: logic below 放到 service 中處理
        if ("add_to_cart".equals(actionValue)) {
            Integer cartId = orderDao.findCartByUserId(userId);
            if (cartId == null) {
                // add new cart to order, order detail
                Integer orderId = orderDao.createOrder(userId, StatusCode.ORDER_STATUS_CART.ordinal(), Integer.valueOf(productId));
                Integer orderDetailId = orderDetailDao.addOrderDetail(orderId, Integer.valueOf(productId), LimitAmount.FIND_PRODUCT_AMOUNT.ordinal());
            } else {
                // add product to order detail
                Integer orderDetailId = orderDetailDao.addOrderDetail(cartId, Integer.valueOf(productId), LimitAmount.FIND_PRODUCT_AMOUNT.ordinal());
                // update cart
                Integer updateSuccess = orderDao.updateOrderTotal(cartId);
            }
        }
        // 在 linebot 中文字訊息顯示 {商品名稱} 已加入購物車
        return lineBotService.createTextMessage(productName + " 已加入購物車");
    }


}
