package com.example.appbot.service;

import com.example.appbot.dao.LogisticDao;
import com.example.appbot.dao.OrderDao;
import com.example.appbot.dao.OrderDetailDao;
import com.example.appbot.dao.PaymentDao;
import com.example.appbot.dto.LogisticDTO;
import com.example.appbot.dto.OrderDetailDTO;
import com.example.appbot.dto.OrderDTO;
import com.example.appbot.dto.PaymentDTO;
import com.example.appbot.enums.LimitAmount;
import com.example.appbot.enums.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final LogisticDao logisticDao;
    private final PaymentDao paymentDao;
    private final OrderDetailDao orderDetailDao;
    private final LineBotService lineBotService;
    private final S3Service s3Service;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderDao orderDao, LogisticDao logisticDao, PaymentDao paymentDao, OrderDetailDao orderDetailDao, LineBotService lineBotService, S3Service s3Service) {
        this.orderDao = orderDao;
        this.logisticDao = logisticDao;
        this.paymentDao = paymentDao;
        this.orderDetailDao = orderDetailDao;
        this.lineBotService = lineBotService;
        this.s3Service = s3Service;
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
            orderDetailDao.addOrderDetail(cartId, Integer.valueOf(productId), LimitAmount.ADD_CART_AMOUNT.ordinal());
        }
        List<OrderDetailDTO> orderDetails = orderDao.updateOrderTotal(cartId);
        logger.info("Updated order details for cartId {}: {}", cartId, orderDetails);
    }

    @Override
    public List<OrderDetailDTO> findOrderDetailListByOrderId(Integer orderId) {
        List<OrderDetailDTO> orderDetailDTO = orderDetailDao.findOrderDetailListByOrderId(orderId);
        // add s3 prefix
        orderDetailDTO.forEach(dto -> {dto.setProductImage(s3Service.getFileUrl(dto.getProductImage()));});
        return orderDetailDTO;
    }

    @Override
    public LogisticDTO findLogisticByOrderId(Integer orderId) {
        return logisticDao.findLogisticByOrderId(orderId);
    }

    @Override
    public PaymentDTO findPaymentByOrderId(Integer orderId) {
        return paymentDao.findPaymentByOrderId(orderId);
    }
}
