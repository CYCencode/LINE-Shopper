package com.example.appbot.service;

import com.example.appbot.dao.LogisticDao;
import com.example.appbot.dao.OrderDao;
import com.example.appbot.dao.OrderDetailDao;
import com.example.appbot.dao.ProductDao;
import com.example.appbot.dto.LogisticDTO;
import com.example.appbot.dto.OrderDetailDTO;
import com.example.appbot.dto.ProductDTO;
import com.example.appbot.exception.CheckoutException;
import com.fasterxml.jackson.databind.JsonNode;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.example.appbot.enums.LimitAmount;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LineBotServiceImpl implements LineBotService {
    @Value("${LINE_PROFILE_URL}")
    private String LINE_PROFILE_URL;
    @Value("${line.bot.channel-token}")
    private String CHANNEL_ACCESS_TOKEN;
    @Value("${web.page.checkout}")
    private String WEB_PAGE_CHECKOUT;
    @Value("${web.page.cart}")
    private String WEB_PAGE_CART;

    private static final Logger logger = LoggerFactory.getLogger(LineBotServiceImpl.class);
    private final ProductDao productDao;
    private final S3Service s3Service;
    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;
    private final LogisticDao logisticDao;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RichMenuService richMenuService;
    private final LineMessagingClient lineMessagingClient;
    public LineBotServiceImpl(ProductDao productDao, S3Service s3Service, OrderDao orderDao, OrderDetailDao orderDetailDao, LogisticDao logisticDao, RestTemplate restTemplate, ObjectMapper objectMapper, RichMenuService richMenuService, LineMessagingClient lineMessagingClient) {
        this.productDao = productDao;
        this.s3Service = s3Service;
        this.orderDao = orderDao;
        this.orderDetailDao = orderDetailDao;
        this.logisticDao = logisticDao;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.richMenuService = richMenuService;
        this.lineMessagingClient = lineMessagingClient;
    }

    @Override
    public Message handleTextMessage(MessageEvent<TextMessageContent> event){
        String userMessage = event.getMessage().getText();
        String userId = event.getSource().getUserId();
        if ("想了解".equals(userMessage)) {
            return createQuickReplyMessage();
        }else if("男裝".equals(userMessage)|| "女裝".equals(userMessage) || "飾品".equals(userMessage)) {
            List<ProductDTO> dtoList = productDao.findProductByCategory(LimitAmount.FIND_PRODUCT_AMOUNT.ordinal(), userMessage);
            return createCarouselMessage(dtoList);
        } else if (userMessage.startsWith("找 ")) {
            userMessage = userMessage.replace("找 ", "");
            List<ProductDTO> dtoList = productDao.findProductByKeyword(userMessage);
            return createCarouselMessage(dtoList);
        } else if ("查看購物車".equals(userMessage)) {
            return createCartQuickReplyMessage(userId, " 點擊按鈕查看您的購物車");
        } else if ("限時促銷".equals(userMessage)) {
            return richMenuService.createCampaignFlexMessage();
        }else if (userMessage.startsWith("ST") && userMessage.length() == 15) {
            String orderNo = userMessage;
            return createSearchOrderTextMessage(orderNo);
        }
        else if ("結帳".equals(userMessage)) {
            try {
                Integer orderId = orderDao.findCartByUserId(userId);
                if (orderId == null) {
                    throw new CheckoutException("購物車內無商品");
                }
                Integer total = orderDao.getTotalByOrderId(orderId);
                String checkoutUrl = String.format("%s?line_user_id=%s&cart_id=%s&total=%s", WEB_PAGE_CHECKOUT,userId, orderId, total);
                return TextMessage.builder()
                        .text("是否進行結帳")
                        .quickReply(
                                QuickReply.builder()
                                        .item(
                                                QuickReplyItem.builder()
                                                        .action(new URIAction("結帳", new URI(checkoutUrl), new URIAction.AltUri(new URI(checkoutUrl))))
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();
            } catch (CheckoutException e) {
                return new TextMessage(e.getMessage());
            }
            catch (Exception e) {
                logger.info(e.getMessage());
                return new TextMessage("不可結帳");
            }

        } else{
            return createTextMessage("請輸入 : '想了解'，查看商品分類。\n或輸入 : '找 關鍵字'，搜尋包含特定關鍵字的商品。");
        }
    }
    public Message createSearchOrderTextMessage(String orderNo) {
        try{
            // 先取得對應的 order_id
            Integer orderId = orderDao.findOrderIdByOrderNo(orderNo);
            if (orderId == null) {
                return createTextMessage("找不到訂單編號為 " + orderNo + " 的訂單。");
            }

            // 獲取訂單明細並計算總消費金額
            List<OrderDetailDTO> orderDetails = orderDetailDao.findOrderDetailListByOrderId(orderId);
            Integer totalAmount = orderDetails.stream()
                    .mapToInt(detail -> detail.getDiscountedPrice() * detail.getQuantity())
                    .sum();

            // 構建購買清單 :
            StringBuilder purchasedItems = new StringBuilder();
            for (OrderDetailDTO detail : orderDetails) {
                purchasedItems.append(detail.getProductName()).append("\n");
            }
            logger.info(String.valueOf(purchasedItems));
            // 獲取物流資訊
            LogisticDTO logistic = logisticDao.searchLogisticByOrderNo(orderNo);
            String status = logistic.getStatus();
            if (status.equals("300")) {
                status = "物流已收單";
            }else if (status.equals("3003")) {
                status = "配送完畢";
            } else if (status.equals("3001")) {
                status = "轉運中";
            } else if (status.equals("3006")) {
                status = "配送中";
            }else{
                status = "物流準備中";
            }
            // 構建回傳訊息
            String responseMessage = String.format("訂單編號: %s\n總消費金額: %d\n購買清單:\n%s訂單狀態: %s",
                    orderNo, totalAmount, purchasedItems, status);

            return new TextMessage(responseMessage);
        } catch (Exception e){
            return createTextMessage("請輸入有效的訂單編號。");
        }
    }

    @Override
    public Message createQuickReplyMessage() {
        QuickReply quickReply = QuickReply.items(
                Arrays.asList(
                        QuickReplyItem.builder().action(new MessageAction("男裝", "男裝")).build(),
                        QuickReplyItem.builder().action(new MessageAction("女裝", "女裝")).build(),
                        QuickReplyItem.builder().action(new MessageAction("飾品", "飾品")).build()
                )
        );

        return TextMessage.builder()
                .text("想了解哪種類型的商品呢？")
                .quickReply(quickReply)
                .build();
    }

    @Override
    public Message createProductButtonsTemplate(ProductDTO productDTO) {
        String imageUrl = s3Service.getFileUrl(productDTO.getImage());
        String title = productDTO.getName();
        String text = productDTO.getPrice().toString();
        Integer product_id = productDTO.getId();
        try{
            ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                    URI.create(imageUrl),
                    title,
                    text,
                    Arrays.asList(
                            new PostbackAction("加入購物車", "action=add_to_cart&product_id="+product_id+"&product_name="+title)
                    )
            );
            return new TemplateMessage("查看商品資訊 : "+ title, buttonsTemplate);
        }catch(Exception e){
            logger.info(e.getMessage());
            return createTextMessage("出現錯誤，請稍後再試。");
        }
    }
    @Override
    public Message createCartQuickReplyMessage(String userId, String text) {
        Integer cartId = orderDao.findCartByUserId(userId);
        String cartUrl = String.format("%s?cart_id=%s", WEB_PAGE_CART, cartId);

        QuickReply quickReply = QuickReply.items(
                Arrays.asList(
                        QuickReplyItem.builder()
                                .action(new URIAction("查看購物車", URI.create(cartUrl), new URIAction.AltUri(URI.create(cartUrl))))
                                .build()
                )
        );

        return TextMessage.builder()
                .text(text)
                .quickReply(quickReply)
                .build();
    }

    @Override
    public Message createTextMessage(String text) {
        return new TextMessage(text);
    }

    @Override
    public Message createCarouselMessage(List<ProductDTO> dtoList) {
        if (dtoList.size() == 0) {
            return new TextMessage("查無此商品");
        }
        List<CarouselColumn> colList = new ArrayList<>();
        dtoList.forEach(dto -> {
            try {
                String imageUrl = s3Service.getFileUrl(dto.getImage());
                String title = dto.getName();
                String text = dto.getPrice().toString();
                String productId = dto.getId().toString();
                colList.add(
                        CarouselColumn.builder()
                                .thumbnailImageUrl(new URI(imageUrl))
                                .title(title)
                                .text(text)
                                .actions(
                                        List.of(
                                                new PostbackAction("加入購物車", "action=add_to_cart&product_id="+ productId +"&product_name="+title),
                                                new MessageAction("結帳", "結帳")
                                        )
                                )
                                .build()
                );
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        });

        return new TemplateMessage(
                "查詢商品",
                CarouselTemplate.builder()
                        .columns(colList)
                        .build()
        );
    }
    @Override
    public String getUserProfile(String userId) {
        String url = LINE_PROFILE_URL + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            return rootNode.path("displayName").asText();
        } catch (Exception e) {
            logger.error("Error parsing user profile: ", e);
            return "";
        }
    }
}
