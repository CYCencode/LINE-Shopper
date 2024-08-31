package com.example.appbot.controller;

import com.example.appbot.dao.OrderDao;
import com.example.appbot.dao.OrderDaoImpl;
import com.example.appbot.dto.CheckoutRequestDTO;
import com.example.appbot.exception.CheckoutException;
import com.example.appbot.service.CheckoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v1")
public class CheckoutController {

    private final OrderDao orderDao;
    private final CheckoutService checkoutService;
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);


    public CheckoutController(OrderDao orderDao, CheckoutService checkoutService) {
        this.orderDao = orderDao;
        this.checkoutService = checkoutService;
    }

    @PostMapping("/order/checkout")
    public ResponseEntity<?> checkout(@RequestBody @Validated CheckoutRequestDTO dto) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer orderId = orderDao.findCartByUserId(dto.getLineUserId());
            if (orderId != null) {
                checkoutService.handleCheckout(dto);
                map.put("success", true);
                map.put("msg", "付款成功");
            } else {
                map.put("success", false);
                map.put("msg", "購物車內無商品");
            }
        } catch (CheckoutException e) {
            map.put("success", false);
            map.put("msg", e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", "購物車系統忙碌中，請稍後再試");
            logger.error(e.getMessage());
        }
        return ResponseEntity.ok(map);
    }
}
