package com.example.appbot.controller;

import com.example.appbot.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1")
public class SearchController {

    private final OrderService orderService;

    public SearchController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/search")
    public ResponseEntity<?> search(@RequestParam(value = "orderNo", required = false) String orderNo,
                                    @RequestParam(value = "page", required = false) Integer page) {
        return ResponseEntity.ok(orderService.findOrder(orderNo, page));
    }
}
