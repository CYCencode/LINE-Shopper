package com.example.appbot.service;

import com.example.appbot.dao.*;
import com.example.appbot.dto.*;
import com.example.appbot.enums.StatusCode;
import com.example.appbot.exception.CheckoutException;
import com.example.appbot.util.EncodingUtil;
import com.example.appbot.util.PostbackDataParser;
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
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Log4j2
public class CheckoutServiceImpl implements CheckoutService{

    @Value("${ecpay.express.create.url}")
    private String EXPRESS_CREATE_URL;

    @Value("${ecpay.merchant.id}")
    private String ECPAY_MERCHANT_ID;

    @Value("${ecpay.hash.key}")
    private String ECPAY_HASH_KEY;

    @Value("${ecpay.hash.iv}")
    private String ECPAY_HASH_IV;

    @Value("${tappay.sandbox.url}")
    private String TAPPAY_SANDBOX_URL;

    @Value("${tappay.partner.key}")
    private String TAPPAY_PARTNER_KEY;

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
    public void handleCheckout(CheckoutRequestDTO crDTO) {
        String msg = "";
        String orderNo = "";
        try {
            Integer orderId = orderDao.findCartByUserId(crDTO.getLineUserId());
            OrderDTO orderDTO = orderDao.findOrderById(orderId);
            if(orderDTO.getOrderNo() == null) {
                orderNo = orderDao.getTodaySerialNumber();
                orderDTO.setOrderNo(orderNo);
                orderDao.updateOrderNoById(orderId, orderNo);
            } else {
                orderNo = orderDTO.getOrderNo();
            }

            verifyLogistic(crDTO, orderDTO);
            verifyPayment(crDTO, orderDTO);
            orderDao.updateOrderStatus(orderId, StatusCode.ORDER_STATUS_PAID.ordinal());
            msg = String.format("訂單 %s 已成立", orderNo);
        } catch (CheckoutException e) {
            msg = String.format("訂單 %s %s", orderNo, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(String.format("%s %s", orderNo, e.getMessage()));
            msg = String.format("訂單 %s 交易失敗", orderNo);
            throw e;
        } finally {
            replyUser(crDTO.getLineUserId(), msg);
        }
    }

    @Override
    public Boolean verifyPayment(CheckoutRequestDTO crDTO, OrderDTO orderDTO) {
        // tappay
        CardHolderDTO cardHolderDTO = CardHolderDTO.builder()
            .phoneNumber(crDTO.getReceiverPhone())
            .name(crDTO.getReceiverName())
            .email(crDTO.getReceiverEmail())
            .build();

        PayByPrimeDTO payByPrimeDto = PayByPrimeDTO.builder()
            .prime(crDTO.getPrime())
            .partnerKey(TAPPAY_PARTNER_KEY)
            .merchantId(TAPPAY_MERCHANT_ID)
            .amount(orderDTO.getTotal())
            .details(orderDTO.getOrderNo())
            .cardholder(cardHolderDTO)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", TAPPAY_PARTNER_KEY);
        HttpEntity<PayByPrimeDTO> requestEntity = new HttpEntity<>(payByPrimeDto, headers);
        ResponseEntity<TappayResultDTO> responseEntity = restTemplate.exchange(TAPPAY_SANDBOX_URL, HttpMethod.POST, requestEntity, TappayResultDTO.class);

        TappayResultDTO tappayResultDto = null;
        if (responseEntity.hasBody()) {
            tappayResultDto = responseEntity.getBody();
            if (tappayResultDto.getStatus() != 0) {
                log.error(String.format("%s %s", orderDTO.getOrderNo(), tappayResultDto.getMsg()));
                throw new CheckoutException("付款交易失敗");
            }
            log.info(tappayResultDto);
            paymentDao.createPayment(orderDTO.getId(), crDTO.getPaymentMethod());
        }

        return true;
    }

    @Override
    public Boolean verifyLogistic(CheckoutRequestDTO crDTO, OrderDTO orderDTO) {
        // ecpay
        Date date = new Date();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("GoodsAmount" , orderDTO.getTotal().toString());
        map.put("GoodsName" , orderDTO.getOrderNo());
        map.put("LogisticsSubType" , "TCAT");
        map.put("LogisticsType" , "HOME");
        map.put("MerchantID", ECPAY_MERCHANT_ID);
        map.put("MerchantTradeDate", sdfTradeDate.format(date));
        map.put("MerchantTradeNo", orderDTO.getOrderNo());
        map.put("ReceiverAddress", crDTO.getReceiverAddress());
        map.put("ReceiverCellPhone", crDTO.getReceiverPhone());
        map.put("ReceiverName", crDTO.getReceiverName());
        map.put("ReceiverZipCode", crDTO.getReceiverZipcode());
        map.put("SenderAddress", "桃園市八德區介壽路二段148號");
        map.put("SenderCellPhone", "0912345678");
        map.put("SenderName", "Brian");
        map.put("SenderZipCode", "33441");
        map.put("ServerReplyURL", "https://gorgeous-apparent-ape.ngrok-free.app");
        String CMV = EncodingUtil.getCalculateCMV(map, ECPAY_HASH_KEY, ECPAY_HASH_IV);
        map.put("CheckMacValue", CMV);

        StringBuilder sb = new StringBuilder();
        for(String key : map.keySet()) {
            sb.append(key).append("=").append(URLEncoder.encode(map.get(key), StandardCharsets.UTF_8)).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.toString());
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(EXPRESS_CREATE_URL, HttpMethod.POST, requestEntity, String.class);

        // parse response and save it to logistics table
        String responseText = response.getBody();
        log.info(responseText);
        String[] resultArray = responseText.split("\\|");
        Integer statusCode = Integer.parseInt(resultArray[0]);
        if (!statusCode.equals(1)) {
            log.error(String.format("%s %s", orderDTO.getOrderNo(), resultArray[1]));
            throw new CheckoutException(String.format("物流交易失敗，%s",resultArray[1]));
        }
        Map<String, String> paramMap = PostbackDataParser.parse(resultArray[1]);
        logisticDao.createLogistic(
            LogisticDTO.builder()
                .orderId(orderDTO.getId())
                .orderNo(paramMap.get("MerchantTradeNo"))
                .status(paramMap.get("RtoCode"))
                .shipping(paramMap.get("LogisticsType"))
                .allPayLogisticId(paramMap.get("AllPayLogisticsID"))
                .bookingNote(paramMap.get("BookingNote"))
                .build()
        );

        return true;
    }

    public void replyUser(String lineUserId, String msg) {
        TextMessage textMessage = new TextMessage(msg);
        PushMessage pushMessage = new PushMessage(lineUserId, textMessage);
        lineMessagingClient.pushMessage(pushMessage);
    }
}
