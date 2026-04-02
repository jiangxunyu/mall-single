package com.mall.mapper;

import com.mall.entity.Order;

public interface OrderMapper {

    void insert(Order order);

    void closeTimeoutOrders();

    void update(Order order);
}