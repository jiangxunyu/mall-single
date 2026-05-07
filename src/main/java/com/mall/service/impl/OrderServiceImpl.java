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
import java.util.HashMap;
import java.util.List;
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
            Map<String,Object> objectMap = (Map<String, Object>) entry.getValue();
            Integer count = Integer.valueOf(objectMap.get("count").toString());

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


    @Override
    public List<Map<String, Object>> listOrders(Long userId) {
        List<Order> orders = orderMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", order.getId());
            map.put("totalPrice", order.getTotalAmount());
            map.put("status", getStatusText(order.getStatus()));
            map.put("statusCode", order.getStatus());
            map.put("createTime", order.getCreateTime());
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("id", order.getId());
        result.put("totalPrice", order.getTotalAmount());
        result.put("status", getStatusText(order.getStatus()));
        result.put("statusCode", order.getStatus());
        result.put("createTime", order.getCreateTime());

        List<OrderItem> items = itemMapper.selectByOrderId(orderId);
        List<Map<String, Object>> itemList = new java.util.ArrayList<>();
        for (OrderItem item : items) {
            Map<String, Object> itemMap = new java.util.HashMap<>();
            Product product = productMapper.selectById(item.getProductId());
            itemMap.put("productName", product != null ? product.getName() : "未知商品");
            itemMap.put("productPrice", item.getPrice());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("subtotal", item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            itemList.add(itemMap);
        }
        result.put("items", itemList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许取消");
        }
        order.setStatus(3);
        orderMapper.update(order);
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "PENDING";
            case 1: return "PAID";
            case 2: return "SHIPPED";
            case 3: return "CANCELLED";
            case 4: return "COMPLETED";
            default: return "UNKNOWN";
        }
    }

    @Override
    public Map<String, Object> listOrdersByPage(Long userId, Integer pageNum, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        int offset = (pageNum - 1) * pageSize;
        List<Order> orders = orderMapper.selectByUserIdWithPage(userId, offset, pageSize);
        Integer total = orderMapper.countByUserId(userId);

        List<Map<String, Object>> list = new java.util.ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", order.getId());
            map.put("totalPrice", order.getTotalAmount());
            map.put("status", getStatusText(order.getStatus()));
            map.put("statusCode", order.getStatus());
            map.put("createTime", order.getCreateTime());
            list.add(map);
        }
        result.put("list", list);
        result.put("total", total);
        return result;
    }
}
