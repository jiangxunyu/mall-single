package com.mall.service.impl;

import com.mall.service.OrderService;
import com.mall.service.SeckillService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    private DefaultRedisScript<Long> script;
    @PostConstruct
    public void init(){
        script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("seckill.lua"));
        script.setResultType(Long.class);
    }

    @Override
    public String doSeckill(Long userId, Long productId){

        String key = "seckill:stock:" + productId;

        Long stock = redisTemplate.opsForValue().decrement(key);

        if(stock < 0){
            return "库存不足";
        }

        orderService.createOrder(userId, productId,1);

        return "秒杀成功";
    }

    @Override
    public String doSeckillLua(Long userId, Long productId){

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList("seckill:stock:" + productId)
        );

        if(result == 0){
            return "已售罄";
        }

        // 👉 这里应该走MQ（企业级）
        orderService.createOrder(userId, productId,1);

        return "秒杀成功";
    }

    @Override
    public void addStock(String key, Long stock) {
        redisTemplate.opsForValue().increment(key, stock);
    }
}