package com.example.appbot.controller;

import com.example.appbot.dto.CampaignDTO;
import com.example.appbot.dto.ProductDTO;
import com.example.appbot.service.CampaignService;
import com.example.appbot.service.LogisticService;
import com.example.appbot.service.OrderService;
import com.example.appbot.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Log4j2
public class SearchController {

    private final ProductService productService;
    private final OrderService orderService;
    private final CampaignService campaignService;
    private final LogisticService logisticService;
    private Validator validator;


    public SearchController(ProductService productService, OrderService orderService, CampaignService campaignService, LogisticService logisticService) {
        this.productService = productService;
        this.orderService = orderService;
        this.campaignService = campaignService;
        this.logisticService = logisticService;
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
    @PostMapping("/product/create")
    public ResponseEntity<?> createProducts(@RequestParam Map<String, String> formData,
                                            @RequestParam("images") List<MultipartFile> images) throws MethodArgumentNotValidException {
        List<ProductDTO> products = new ArrayList<>();
        int index = 0;
        while (formData.containsKey("products[" + index + "].name")) {
            ProductDTO product = new ProductDTO();
            Integer price = Integer.parseInt(formData.get("products[" + index + "].price"));
            Integer stock = Integer.parseInt(formData.get("products[" + index + "].stock"));
            if (price < 1 || stock < 1) {
                throw new IllegalArgumentException("價格和庫存必須大於等於1");
            }
            product.setName(formData.get("products[" + index + "].name"));
            product.setPrice(price);
            product.setStock(stock);
            product.setCategory(formData.get("products[" + index + "].category"));
            products.add(product);
            index++;
        }

        List<Integer> productIds = productService.createProducts(products, images);
        Map<String, Object> response = new HashMap<>();
        response.put("data", productIds);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<?> searchCampaign(@RequestBody @Validated CampaignDTO campaignDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", campaignService.findCampaignList(campaignDTO));
        return ResponseEntity.ok(map);
    }

    @PostMapping("/campaign/create")
    public ResponseEntity<?> createCampaign(@RequestBody @Validated CampaignDTO campaignDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", campaignService.createCampaign(campaignDTO));
        return ResponseEntity.ok(map);
    }

    @PostMapping(path = "/ecpayServerReply", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> ecpayServerReply(@RequestBody MultiValueMap<String, String> map) {
        logisticService.updateLogisticStatusByOrderNo(map);
        return ResponseEntity.ok("1|OK");
    }
}
