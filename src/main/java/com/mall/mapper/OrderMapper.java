package com.mall.mapper;

import com.mall.po.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {

    void insert(Order order);

    void closeTimeoutOrders();

    void update(Order order);

    List<Order> selectByUserId(Long userId);

    Order selectById(Long orderId);

    List<Order> selectByUserIdWithPage(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countByUserId(@Param("userId") Long userId);
}