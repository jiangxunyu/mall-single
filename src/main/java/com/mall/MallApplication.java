package com.mall;

import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@MapperScan("com.mall.mapper")
public class MallApplication {

    @Autowired
    private RedisTemplate redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(MallApplication.class, args);
    }

    /**
     * 预热库存
     */
    @PostConstruct
    public void init(){
        redisTemplate.opsForValue().set("seckill:stock:1", "100");
    }
}