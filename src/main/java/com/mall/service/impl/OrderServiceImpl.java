package com.mall.service.impl;

import com.mall.po.entity.Order;
import com.mall.po.entity.OrderItem;
import com.mall.po.entity.Product;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
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


    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, Long productId,Integer count){

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(0);
        order.setCreateTime(new Date());

        Product product = productMapper.selectById(productId);

        // ⚠️ 扣库存（重点）
        int rows = productMapper.deductStock(productId, count);

        if(rows == 0){
            throw new RuntimeException("库存不足");
        }

        // 计算金额
        BigDecimal total = BigDecimal.ZERO;

        BigDecimal price = product.getPrice() == null
                ? BigDecimal.ZERO
                : product.getPrice();

        total = total.add(price.multiply(BigDecimal.valueOf(count)));
        //总金额
        order.setTotalAmount(total);
        orderMapper.insert(order);

        // 插入订单项
        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(productId);
        item.setQuantity(count);
        item.setPrice(price);

        itemMapper.insert(item);

        return order.getId();
    }

    @Override
    @Transactional
    public Long createByCart(Long userId) {

        // 1. 获取购物车
        Map<Object, Object> cart = cartService.list(userId);

        if(cart.isEmpty()){
            throw new RuntimeException("购物车为空");
        }

        // 2. 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(0); // 未支付
        order.setCreateTime(new Date());

        // 计算金额
        BigDecimal total = BigDecimal.ZERO;

        // 3. 处理订单项
        for(Object key : cart.keySet()){

            Long productId = Long.valueOf(key.toString());
            Integer count = Integer.valueOf(cart.get(key).toString());

            Product product = productMapper.selectById(productId);

            // ⚠️ 扣库存（重点）
            int rows = productMapper.deductStock(productId, count);

            if(rows == 0){
                throw new RuntimeException("库存不足");
            }

            BigDecimal price = product.getPrice() == null
                    ? BigDecimal.ZERO
                    : product.getPrice();

            int cnt = count == null ? 0 : count;

            total = total.add(price.multiply(BigDecimal.valueOf(cnt)));

            // 插入订单项
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(productId);
            item.setQuantity(count);
            item.setPrice(price);

            // 4. 更新总金额
            order.setTotalAmount(total);
            orderMapper.insert(order);

            // 插入订单id
            item.setOrderId(order.getId());
            itemMapper.insert(item);
        }

        // 5. 清空购物车
        redisTemplate.delete("cart:" + userId);

        return order.getId();
    }

    public void closeTimeoutOrders(){
        orderMapper.closeTimeoutOrders();
    }
}