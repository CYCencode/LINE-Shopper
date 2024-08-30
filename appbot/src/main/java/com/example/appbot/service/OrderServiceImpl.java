package com.example.appbot.service;

import com.example.appbot.dao.OrderDao;
import com.example.appbot.dao.OrderDetailDao;
import com.example.appbot.dto.OrderDetailDTO;
import com.example.appbot.dto.OrderDTO;
import com.example.appbot.enums.LimitAmount;
import com.example.appbot.enums.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;
    private final LineBotService lineBotService;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    public OrderServiceImpl(OrderDao orderDao, OrderDetailDao orderDetailDao, LineBotService lineBotService) {
        this.orderDao = orderDao;
        this.orderDetailDao = orderDetailDao;
        this.lineBotService = lineBotService;
    }

    @Override
    public List<OrderDTO> findOrder(String orderNo, Integer page) {
        return orderDao.findOrder(orderNo, page);
    }

    @Override
    public Integer findCartByUserId(String lineUserId) {
        return orderDao.findCartByUserId(lineUserId);
    }

    @Override
    public Integer findOrderByUserId(String lineUserId) {
        return orderDao.findOrderByUserId(lineUserId);
    }

    @Override
    public void addToCart(String userId, String productId) {
        Integer cartId = orderDao.findCartByUserId(userId);
        String userName = lineBotService.getUserProfile(userId);
        if (cartId == null) {
            cartId = orderDao.createOrder(userId,userName, StatusCode.ORDER_STATUS_CART.ordinal());
        }
        Integer detailCount = orderDetailDao.findCountOrderDetailByOrderId(cartId, Integer.valueOf(productId));
        if (detailCount > 0) {
            orderDetailDao.incQtyOrderDetailByOrderId(cartId, Integer.valueOf(productId));
        } else {
            orderDetailDao.addOrderDetail(cartId, Integer.valueOf(productId), LimitAmount.FIND_PRODUCT_AMOUNT.ordinal());
        }
        List<OrderDetailDTO> orderDetails = orderDao.updateOrderTotal(cartId);
        logger.info("Updated order details for cartId {}: {}", cartId, orderDetails);
    }

    @Override
    public List<OrderDetailDTO> findOrderDetailListByOrderId(Integer orderId) {
        return orderDetailDao.findOrderDetailListByOrderId(orderId);
    }
}
