package com.mall.service;

public interface OrderService {

    Long createOrder(Long userId, Long productId, Integer count);

    void closeTimeoutOrders();

    Long createByCart(Long userId);
}