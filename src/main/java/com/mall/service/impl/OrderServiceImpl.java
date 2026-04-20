package com.mall.service.impl;

import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import com.mall.po.entity.Order;
import com.mall.po.entity.OrderItem;
import com.mall.po.entity.Product;
import com.mall.service.CartService;
import com.mall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper itemMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, Long productId, Integer count) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 扣库存（重点）
        int rows = productMapper.deductStock(productId, count);
        if (rows == 0) {
            throw new RuntimeException("库存不足");
        }

        BigDecimal price = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
        BigDecimal total = price.multiply(BigDecimal.valueOf(count));

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(0);
        order.setCreateTime(new Date());
        order.setTotalAmount(total);
        orderMapper.insert(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(productId);
        item.setQuantity(count);
        item.setPrice(price);
        itemMapper.insert(item);

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createByCart(Long userId) {
        Map<Object, Object> cart = cartService.list(userId);
        if (cart == null || cart.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(0);
        order.setCreateTime(new Date());
        order.setTotalAmount(BigDecimal.ZERO);
        orderMapper.insert(order);

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Object, Object> entry : cart.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());
            Integer count = Integer.valueOf(entry.getValue().toString());

            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在: " + productId);
            }

            int rows = productMapper.deductStock(productId, count);
            if (rows == 0) {
                throw new RuntimeException("库存不足");
            }

            BigDecimal price = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
            total = total.add(price.multiply(BigDecimal.valueOf(count)));

            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(productId);
            item.setQuantity(count);
            item.setPrice(price);
            itemMapper.insert(item);
        }

        order.setTotalAmount(total);
        orderMapper.update(order);
        redisTemplate.delete("cart:" + userId);
        return order.getId();
    }

    @Override
    public void closeTimeoutOrders() {
        orderMapper.closeTimeoutOrders();
    }
}
