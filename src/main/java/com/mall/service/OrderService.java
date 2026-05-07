package com.mall.service;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Long createOrder(Long userId, Long productId, Integer count);

    void closeTimeoutOrders();

    Long createByCart(Long userId);

    List<Map<String, Object>> listOrders(Long userId);

    Map<String, Object> getOrderDetail(Long orderId);

    void cancelOrder(Long orderId);

    Map<String, Object> listOrdersByPage(Long userId, Integer pageNum, Integer pageSize);

}