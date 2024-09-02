package com.example.appbot.dao;

import com.example.appbot.dto.OrderDTO;
import com.example.appbot.dto.OrderDetailDTO;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

public interface OrderDao {
    Integer createOrder(String lineUserId,String lineUserName,Integer orderStatus);
    Integer findCartByUserId(String lineUserId);
    List<OrderDetailDTO> updateOrderTotal(Integer cartId);
    Integer updateOrderStatus(Integer cartId,Integer orderStatus);
    String getTodaySerialNumber();
    OrderDTO findOrderById(Integer id);
    Integer updateOrderNoById(Integer id, String orderNo);
    Integer findOrderIdByOrderNo(String orderNo, String userId);
    List<OrderDTO> findOrder(String orderNo, Integer page);
    Integer getTotalByOrderId(Integer orderId);
}