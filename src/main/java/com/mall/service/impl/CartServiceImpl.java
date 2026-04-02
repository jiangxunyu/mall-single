package com.mall.service.impl;

import com.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

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
        return redisTemplate.opsForHash().entries(getKey(userId));
    }

    @Override
    public void remove(Long userId, Long productId) {
        redisTemplate.opsForHash().delete(getKey(userId), productId.toString());
    }
}