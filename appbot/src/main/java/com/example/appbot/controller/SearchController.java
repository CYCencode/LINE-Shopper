package com.example.appbot.controller;

import com.example.appbot.dao.CampaignDao;
import com.example.appbot.dto.CampaignDTO;
import com.example.appbot.service.CampaignService;
import com.example.appbot.service.OrderService;
import com.example.appbot.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Log4j2
public class SearchController {

    private final ProductService productService;
    private final OrderService orderService;
    private final CampaignService campaignService;

    public SearchController(ProductService productService, OrderService orderService, CampaignService campaignService) {
        this.productService = productService;
        this.orderService = orderService;
        this.campaignService = campaignService;
    }

    @GetMapping("/product/searchAll")
    public ResponseEntity<?> searchAllProduct() {
        Map<String, Object> map = new HashMap<>();
        map.put("data", productService.findAllProduct());
        return ResponseEntity.ok(map);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> searchAllProduct(@PathVariable Integer id) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", productService.findProductById(id));
        return ResponseEntity.ok(map);
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

    @GetMapping("/orderPayment/search")
    public ResponseEntity<?> searchOrderPayment(@RequestParam("order_id") Integer orderId) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", orderService.findPaymentByOrderId(orderId));
        return ResponseEntity.ok(map);
    }

    @GetMapping("/orderLogistic/search")
    public ResponseEntity<?> searchOrderLogistic(@RequestParam("order_id") Integer orderId) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", orderService.findLogisticByOrderId(orderId));
        return ResponseEntity.ok(map);
    }

    @PostMapping("/campaign/search")
    public ResponseEntity<?> searchCampaign(@RequestBody CampaignDTO campaignDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", campaignService.findCampaignList(campaignDTO));
        return ResponseEntity.ok(map);
    }

    @PostMapping("/campaign/create")
    public ResponseEntity<?> createCampaign(@RequestBody CampaignDTO campaignDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", campaignService.createCampaign(campaignDTO));
        return ResponseEntity.ok(map);
    }
}
