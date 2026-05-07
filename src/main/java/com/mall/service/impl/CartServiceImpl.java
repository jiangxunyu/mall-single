package com.mall.service.impl;

import com.mall.mapper.ProductMapper;
import com.mall.po.entity.Product;
import com.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductMapper productMapper;

    private String getKey(Long userId){
        return "cart:" + userId;
    }

    @Override
    public void add(Long userId, Long productId, Integer count) {
        redisTemplate.opsForHash().increment(
                getKey(userId),
                productId.toString(),
                count
        );
    }

    @Override
    public Map<Object, Object> list(Long userId) {
        Map<Object, Object> cartItems = redisTemplate.opsForHash().entries(getKey(userId));
        Map<Object, Object> result = new HashMap<>();

        for (Map.Entry<Object, Object> entry : cartItems.entrySet()) {
            Long productId = Long.parseLong(entry.getKey().toString());
            Integer count = Integer.parseInt(entry.getValue().toString());

            Product product = productMapper.selectById(productId);
            if (product != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", product.getName());
                item.put("price", product.getPrice() != null ? product.getPrice().doubleValue() : 0);
                item.put("count", count);
                result.put(productId.toString(), item);
            }
        }

        return result;
    }

    @Override
    public void remove(Long userId, Long productId) {
        redisTemplate.opsForHash().delete(getKey(userId), productId.toString());
    }
}