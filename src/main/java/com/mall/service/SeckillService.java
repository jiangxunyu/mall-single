package com.mall.service;

public interface SeckillService {
    String doSeckill(Long userId, Long productId);

    String doSeckillLua(Long userId, Long productId);

    void addStock(String key, Long stock);
}