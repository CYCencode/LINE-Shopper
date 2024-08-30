package com.example.appbot.service;

import com.example.appbot.dao.OrderDao;
import com.example.appbot.dao.ProductDao;
import com.example.appbot.dto.ProductDTO;
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
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final LineMessagingClient lineMessagingClient;
    public LineBotServiceImpl(ProductDao productDao, S3Service s3Service, OrderDao orderDao, RestTemplate restTemplate, ObjectMapper objectMapper, LineMessagingClient lineMessagingClient) {
        this.productDao = productDao;
        this.s3Service = s3Service;
        this.orderDao = orderDao;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.lineMessagingClient = lineMessagingClient;
    }

    @Override
    public Message handleTextMessage(MessageEvent<TextMessageContent> event){
        String userMessage = event.getMessage().getText();
        String userId = event.getSource().getUserId();
        if ("想了解".equals(userMessage)) {
            return createQuickReplyMessage();
        }else if("男裝".equals(userMessage)|| "女裝".equals(userMessage) || "飾品".equals(userMessage)) {
            ProductDTO productDTO = productDao.findProductByCategory(LimitAmount.FIND_PRODUCT_AMOUNT.ordinal(), userMessage).get(0);
            return createProductButtonsTemplate(productDTO);
        } else if (userMessage.startsWith("找 ")) {
            userMessage = userMessage.replace("找 ", "");
            List<ProductDTO> dtoList = productDao.findProductByKeyword(userMessage);
            return createCarouselMessage(dtoList);
        } else if ("查看購物車".equals(userMessage)) {
            return createCartQuickReplyMessage(userId, " 點擊按鈕查看您的購物車");
        } else if ("結帳".equals(userMessage)) {
            try {
                Integer orderId = orderDao.findCartByUserId(userId);
                if (orderId == null) {
                    throw new RuntimeException("購物車為空");
                }

                String checkoutUrl = String.format("%s?line_user_id=%s&cart_id=%s", WEB_PAGE_CHECKOUT,userId, orderId);
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
            } catch (RuntimeException e) {
                return new TextMessage(e.getMessage());
            }
            catch (Exception e) {
                return new TextMessage("不可結帳");
            }

        } else{
            return createTextMessage("請輸入 : 想了解，查看可以搜尋的類別");
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
        Integer cartId = orderDao.findOrderByUserId(userId);
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
