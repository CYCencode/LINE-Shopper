package com.example.appbot.controller;

import com.example.appbot.dto.CheckoutRequestDTO;
import com.example.appbot.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/order/checkout")
    public ResponseEntity<?> checkout(@RequestBody @Validated CheckoutRequestDTO dto) {
        checkoutService.handleCheckout(dto);
        return ResponseEntity.ok(dto);
    }
}
