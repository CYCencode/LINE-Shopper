package com.example.appbot.controller;

import com.example.appbot.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Log4j2
public class SearchController {

    private final OrderService orderService;

    public SearchController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/search")
    public ResponseEntity<?> searchOrder(@RequestParam(value = "orderNo", required = false) String orderNo,
                                    @RequestParam(value = "page", required = false) Integer page) {
        return ResponseEntity.ok(orderService.findOrder(orderNo, page));
    }

    @GetMapping("/orderDetail/search")
    public ResponseEntity<?> searchOrderDetailList(@RequestParam("order_id") Integer orderId) {
        return ResponseEntity.ok(
            orderService.findOrderDetailListByOrderId(orderId)
        );
    }
}
