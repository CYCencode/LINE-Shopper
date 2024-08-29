package com.example.appbot.service;

import com.example.appbot.dao.*;
import com.example.appbot.dto.CardHolderDTO;
import com.example.appbot.dto.CheckoutRequestDTO;
import com.example.appbot.dto.PayByPrimeDTO;
import com.example.appbot.util.EncodingUtil;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Log4j2
public class CheckoutServiceImpl implements CheckoutService{

    @Value("${ecpay.express.create.url}")
    private String expressCreateURL;

    @Value("${ecpay.merchant.id}")
    private String merchantId;

    @Value("${ecpay.hash.key}")
    private String hashKey;

    @Value("${ecpay.hash.iv}")
    private String hashIV;

    @Value("${tappay.sandbox.url}")
    private String TAPPAY_SANDBOX_URL;

    @Value("${tappay.partner.id}")
    private String TAPPAY_PARTNER_ID;

    @Value("${tappay.merchant.id}")
    private String TAPPAY_MERCHANT_ID;


    private static  final SimpleDateFormat sdfTradeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final SimpleDateFormat sdfTradeNo = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;
    private final PaymentDao paymentDao;
    private final LogisticDao logisticDao;
    private final RestTemplate restTemplate;
    private final LineMessagingClient lineMessagingClient;

    public CheckoutServiceImpl(OrderDao orderDao, OrderDetailDao orderDetailDao, PaymentDao paymentDao, LogisticDao logisticDao, RestTemplate restTemplate, LineMessagingClient lineMessagingClient) {
        this.orderDao = orderDao;
        this.orderDetailDao = orderDetailDao;
        this.paymentDao = paymentDao;
        this.logisticDao = logisticDao;
        this.restTemplate = restTemplate;
        this.lineMessagingClient = lineMessagingClient;
    }

    @Override
    @Transactional
    public void handleCheckout(CheckoutRequestDTO dto) {
        //        Integer orderId = orderDao.findCartByUserId(dto.getLineUserId());
        //        Integer totalPrice = orderDetailDao.calcCartTotal(orderId);
        Integer orderId=89;
        Integer totalPrice=123;

        verifyLogistic(dto, orderId, totalPrice);
        verifyPayment(dto, orderId, totalPrice);
        // updateStatus

        replyUser(dto.getLineUserId(), "123");
    }

    @Override
    public Boolean verifyPayment(CheckoutRequestDTO dto, Integer orderId, Integer totalPrice) {
        // tappay
        CardHolderDTO cardHolderDTO = CardHolderDTO.builder()
            .phoneNumber(dto.getReceiverPhone())
            .name(dto.getReceiverName())
            .email(dto.getReceiverEmail())
            .build();

        PayByPrimeDTO payByPrimeDto = PayByPrimeDTO.builder()
            .prime(dto.getPrime())
            .partnerKey(TAPPAY_PARTNER_ID)
            .merchantId(TAPPAY_MERCHANT_ID)
            .amount(totalPrice)
            .details("")
            .cardholder(cardHolderDTO)
            .build();

//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("x-api-key", TAPPAY_PARTNER_ID);
//
//        HttpEntity<PayByPrimeDTO> requestEntity = new HttpEntity<>(payByPrimeDto, headers);
//        ResponseEntity<TappayResultDTO> responseEntity = restTemplate.exchange(TAPPAY_SANDBOX_URL, HttpMethod.POST, requestEntity, TappayResultDTO.class);
//
//        TappayResultDTO tappayResultDto = null;
//        if (responseEntity.hasBody()) {
//            tappayResultDto = responseEntity.getBody();
//            if (tappayResultDto.getStatus() != 0) {
//                throw new RuntimeException("payment");
//            }
////            paymentDao.createPayment(orderId, dto.getPaymentMethod());
//        }

        return true;
    }

    @Override
    public Boolean verifyLogistic(CheckoutRequestDTO dto, Integer orderId, Integer totalPrice) {
        // ecpay
        Date date = new Date();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("GoodsAmount" , totalPrice.toString());
        map.put("GoodsName" , orderId.toString());
        map.put("LogisticsSubType" , "TCAT");
        map.put("LogisticsType" , "HOME");
        map.put("MerchantID", merchantId);
        map.put("MerchantTradeDate", sdfTradeDate.format(date));
        map.put("MerchantTradeNo", "BC"+sdfTradeNo.format(date));
        map.put("ReceiverAddress", dto.getReceiverAddress());
        map.put("ReceiverCellPhone", dto.getReceiverPhone());
        map.put("ReceiverName", dto.getReceiverName());
        map.put("ReceiverZipCode", dto.getReceiverZipcode());
        map.put("SenderAddress", "桃園市八德區介壽路二段148號");
        map.put("SenderCellPhone", "0912345678");
        map.put("SenderName", "Brian");
        map.put("SenderZipCode", "33441");
        map.put("ServerReplyURL", "https://gorgeous-apparent-ape.ngrok-free.app");
        String CMV = EncodingUtil.getCalculateCMV(map, hashKey, hashIV);
        map.put("CheckMacValue", CMV);

        StringBuilder sb = new StringBuilder();
        for(String key : map.keySet()) {
            sb.append(key).append("=").append(URLEncoder.encode(map.get(key), StandardCharsets.UTF_8)).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.toString());
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(expressCreateURL, HttpMethod.POST, requestEntity, String.class);

        // parse
        String responseText = response.getBody();
        String[] resultArray = responseText.split("\\|");
        String statusCode = resultArray[0];
        if (!statusCode.equals("1")) {
            throw new RuntimeException("logistic");
        }
        String params = resultArray[1];
        Map<String, String> paramMap = new HashMap<>();
        for(String param : params.split("&")) {
            String[] pair = param.split("=");
            String key = pair[0];
            String val = "";
            if(pair.length > 1) {
                val = pair[1];
            }
            paramMap.put(key,val);
        }

        log.info(responseText);
//        logisticDao.createLogistic(
//            LogisticDTO.builder()
//                .orderId(orderId)
//                .orderNo(paramMap.get("MerchantTradeNo"))
//                .status(paramMap.get("RtoCode"))
//                .shipping(paramMap.get("LogisticsType"))
//                .allPayLogisticId(paramMap.get("AllPayLogisticsID"))
//                .bookingNote(paramMap.get("BookingNote"))
//                .build()
//        );

        return true;
    }

    public void replyUser(String lineUserId, String orderNo) {
        TextMessage textMessage = new TextMessage(String.format("訂單 %s 已成立", orderNo));
        PushMessage pushMessage = new PushMessage(lineUserId, textMessage);
        lineMessagingClient.pushMessage(pushMessage);
    }
}
