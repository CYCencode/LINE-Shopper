package com.example.appbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("hello world");
    }
}
