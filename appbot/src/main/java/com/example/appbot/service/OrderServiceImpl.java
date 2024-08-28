package com.example.appbot.service;

import com.example.appbot.dao.OrderDao;
import com.example.appbot.dao.OrderDetailDao;
import com.example.appbot.enums.LimitAmount;
import com.example.appbot.enums.StatusCode;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;

    public OrderServiceImpl(OrderDao orderDao, OrderDetailDao orderDetailDao) {
        this.orderDao = orderDao;
        this.orderDetailDao = orderDetailDao;
    }
    @Override
    public void addToCart(String userId, String productId) {
        Integer cartId = orderDao.findCartByUserId(userId);
        if (cartId == null) {
            Integer orderId = orderDao.createOrder(userId, StatusCode.ORDER_STATUS_CART.ordinal(), Integer.valueOf(productId));
            orderDetailDao.addOrderDetail(orderId, Integer.valueOf(productId), LimitAmount.FIND_PRODUCT_AMOUNT.ordinal());
        } else {
            orderDetailDao.addOrderDetail(cartId, Integer.valueOf(productId), LimitAmount.FIND_PRODUCT_AMOUNT.ordinal());
            orderDao.updateOrderTotal(cartId);
        }
    }
}
