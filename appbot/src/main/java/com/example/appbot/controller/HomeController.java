package com.example.appbot.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<?> home(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/admin/order.html");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
