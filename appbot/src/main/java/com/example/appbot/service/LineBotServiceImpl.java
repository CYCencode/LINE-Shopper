package com.example.appbot.service;

import com.example.appbot.dao.ProductDao;
import com.example.appbot.dto.ProductDTO;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@Service
public class LineBotServiceImpl implements LineBotService {
    @Value("${FIND_PRODUCT_AMOUNT}")
    private Integer FIND_PRODUCT_AMOUNT;
    @Value("${DOMAIN_NAME_URL}")
    private String DOMAIN_NAME_URL;
    private static final Logger logger = LoggerFactory.getLogger(LineBotServiceImpl.class);
    private final ProductDao productDao;
    private final S3Service s3Service;

    public LineBotServiceImpl(ProductDao productDao, S3Service s3Service) {
        this.productDao = productDao;
        this.s3Service = s3Service;
    }
    @Override
    public Message handleTextMessage(MessageEvent<TextMessageContent> event){
        String userMessage = event.getMessage().getText();
        String userId = event.getSource().getUserId();
        if ("想了解".equals(userMessage)) {
            return createQuickReplyMessage();
        }else if("男裝".equals(userMessage)|| "女裝".equals(userMessage) || "飾品".equals(userMessage)){
            // since button template only accept one entry, get the first product
            ProductDTO productDTO = productDao.findProductByCategory(FIND_PRODUCT_AMOUNT, userMessage).get(0);
            return createButtonsTemplateMessage(productDTO);
        }else{
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
    public Message createButtonsTemplateMessage(ProductDTO productDTO) {
        String category = productDTO.getCategory();
        String imageUrl = s3Service.getFileUrl(productDTO.getImage());
        String title = productDTO.getName();
        String text = productDTO.getPrice().toString();
        Integer product_id = productDTO.getId();
        String checkoutUrl = DOMAIN_NAME_URL+"/checkout.html";

        try{
            ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                    URI.create(imageUrl),
                    title,
                    text,
                    Arrays.asList(
                            new MessageAction("加入購物車", title + " 已加入購物車"),
                            new URIAction("結帳",new URI(checkoutUrl), new URIAction.AltUri(new URI("https://open.spotify.com")))
                    )
            );
            return new TemplateMessage("查看商品資訊 : "+ title, buttonsTemplate);
        }catch(URISyntaxException e){
            logger.info(e.getMessage());
            return createTextMessage("出現錯誤，請稍後再試。");
        }
    }
    @Override
    public Message createTextMessage(String text) {
        return new TextMessage(text);
    }
}
