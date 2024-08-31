package com.example.appbot.service;

import com.example.appbot.dao.ProductDao;
import com.example.appbot.dto.ProductDTO;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.flex.component.Box;
import com.linecorp.bot.model.message.flex.component.Button;
import com.linecorp.bot.model.message.flex.component.Image;
import com.linecorp.bot.model.message.flex.component.Text;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Carousel;
import com.linecorp.bot.model.message.flex.unit.FlexFontSize;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexMarginSize;
import com.linecorp.bot.model.richmenu.RichMenu;
import com.linecorp.bot.model.richmenu.RichMenuArea;
import com.linecorp.bot.model.richmenu.RichMenuBounds;
import com.linecorp.bot.model.richmenu.RichMenuSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class RichMenuService {
    private static final Logger logger = LoggerFactory.getLogger(RichMenuService.class);
    private LineMessagingClient lineMessagingClient;
    private RestTemplate restTemplate;
    private ProductDao productDao;
    private S3Service s3Service;
    @Value("${line.bot.channel-token}")
    private String channelToken;
    public RichMenuService(LineMessagingClient lineMessagingClient, RestTemplate restTemplate, ProductDao productDao, S3Service s3Service) {
        this.lineMessagingClient = lineMessagingClient;
        this.restTemplate = restTemplate;
        this.productDao=productDao;
        this.s3Service=s3Service;
    }

    @PostConstruct
    public void createRichMenu() {
        logger.info("Starting to create Rich Menu...");
        try {
            RichMenu richMenu = RichMenu.builder()
                    .name("Half-Screen Rich Menu")
                    .chatBarText("輸入想了解，查看商品分類")
                    .areas(createRichMenuAreas())
                    .selected(true)
                    .size(new RichMenuSize(2500, 843))
                    .build();

            String richMenuId = lineMessagingClient.createRichMenu(richMenu).get().getRichMenuId();
            uploadRichMenuImage(richMenuId);
            lineMessagingClient.setDefaultRichMenu(richMenuId).get();
        } catch (Exception e) {
            logger.error("Error creating Rich Menu", e);
        }
    }

    private List<RichMenuArea> createRichMenuAreas() {
        return Arrays.asList(
                new RichMenuArea(
                        new RichMenuBounds(0, 0, 833, 843),
                        new MessageAction("購物車", "查看購物車")
                ),
                new RichMenuArea(
                        new RichMenuBounds(833, 0, 834, 843),
                        new MessageAction("結帳", "結帳")
                ),
                new RichMenuArea(
                        new RichMenuBounds(1667, 0, 833, 843),
                        new MessageAction("限時促銷", "限時促銷")
                )
        );
    }

    public void uploadRichMenuImage(String richMenuId) throws Exception {
        // 使用 ClassPathResource 加載 resources 目錄中的圖片
        ClassPathResource imageResource = new ClassPathResource("static/richmenu.png");
        byte[] imageBytes;

        try (InputStream is = imageResource.getInputStream()) {
            imageBytes = is.readAllBytes();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG); // 假設圖片格式是 PNG
        headers.setBearerAuth(channelToken);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(imageBytes, headers);

        String lineApiUrl = "https://api-data.line.me/v2/bot/richmenu/" + richMenuId + "/content";
        ResponseEntity<String> response = restTemplate.exchange(lineApiUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Rich Menu image uploaded successfully!");
        } else {
            logger.error("Failed to upload Rich Menu image: " + response.getBody());
            throw new RuntimeException("Failed to upload Rich Menu image");
        }
    }
    public FlexMessage createCampaignFlexMessage() {
        List<ProductDTO> campaignProducts = productDao.findCampaign();

        Bubble[] bubbles = campaignProducts.stream().map(product -> Bubble.builder()
                .hero(Image.builder()
                        .url(URI.create(s3Service.getFileUrl(product.getImage()))) // 商品圖片URL
                        .size(Image.ImageSize.FULL_WIDTH)
                        .aspectRatio("20:13")
                        .aspectMode(Image.ImageAspectMode.Cover)
                        .build())
                .body(Box.builder()
                        .layout(FlexLayout.VERTICAL)
                        .contents(List.of(
                                Text.builder()
                                        .text(product.getName()) // 商品標題
                                        .weight(Text.TextWeight.BOLD)
                                        .size(FlexFontSize.LG)
                                        .build(),
                                Text.builder()
                                        .text("NT$ " + product.getPrice()) // 商品價格
                                        .weight(Text.TextWeight.BOLD)
                                        .size(FlexFontSize.XL)
                                        .margin(FlexMarginSize.MD)
                                        .build()
                        ))
                        .build())
                .footer(Box.builder()
                        .layout(FlexLayout.VERTICAL)
                        .spacing(FlexMarginSize.SM)
                        .contents(List.of(
                                Button.builder()
                                        .style(Button.ButtonStyle.LINK)
                                        .height(Button.ButtonHeight.SMALL)
                                        .action(new MessageAction("加入購物車", "加入購物車 " + product.getId()))
                                        .build()
                        ))
                        .build())
                .build()).toArray(Bubble[]::new);

        Carousel carousel = Carousel.builder()
                .contents(List.of(bubbles))
                .build();

        return new FlexMessage("限時促銷", carousel);
    }
}
