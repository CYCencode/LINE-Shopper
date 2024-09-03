package com.example.appbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // query參數有錯
    @ExceptionHandler
    public ResponseEntity<?> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", e.getMessage());
        return ResponseEntity.badRequest().body(map);
    }

    // validation產生錯誤
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("msg", "Body參數有誤");
        return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("msg", e.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    // sql 語法錯誤
    @ExceptionHandler
    public ResponseEntity<?> badSqlGrammarExceptionHandler(BadSqlGrammarException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "Internal Server Error");
        return ResponseEntity.internalServerError().body(map);
    }

    // 404
    @ExceptionHandler
    public ResponseEntity<?> noResourceFoundExceptionHandler(NoResourceFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<?> exceptionHandler(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "error");
        return ResponseEntity.badRequest().body(map);
    }
}
