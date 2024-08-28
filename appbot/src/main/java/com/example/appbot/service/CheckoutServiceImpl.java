package com.example.appbot.service;

import com.example.appbot.dao.LogisticDao;
import com.example.appbot.dao.PaymentDao;
import com.example.appbot.dto.CheckoutRequestDTO;
import com.example.appbot.dto.LogisticDTO;
import com.example.appbot.util.EncodingUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
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

    private static  final SimpleDateFormat sdfTradeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final SimpleDateFormat sdfTradeNo = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private final PaymentDao paymentDao;
    private final LogisticDao logisticDao;
    private final RestTemplate restTemplate;

    public CheckoutServiceImpl(PaymentDao paymentDao, LogisticDao logisticDao, RestTemplate restTemplate) {
        this.paymentDao = paymentDao;
        this.logisticDao = logisticDao;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public void handleCheckout(CheckoutRequestDTO dto) {

    }

    @Override
    public Boolean verifyPayment(CheckoutRequestDTO dto) {
        return true;
    }

    @Override
    public Boolean verifyLogistic(CheckoutRequestDTO dto) {
        Date date = new Date();
        Integer orderId=1;
        Integer totalPrice=19;
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

        log.info(paramMap.get("MerchantTradeNo"));
        log.info(paramMap.get("BookingNote"));
//        logisticDao.createLogistic(
//            LogisticDTO.builder()
//                .build()
//        );

        return true;
    }
}
