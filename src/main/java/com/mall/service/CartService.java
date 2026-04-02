package com.mall.service;

import java.util.Map;

public interface CartService {
    void add(Long userId, Long productId, Integer count);
    Map<Object, Object> list(Long userId);
    void remove(Long userId, Long productId);
}
