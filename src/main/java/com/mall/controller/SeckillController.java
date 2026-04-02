package com.mall.controller;

import com.mall.entity.Result;
import com.mall.security.JwtUtil;
import com.mall.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 秒杀逻辑
     * @param userId
     * @param productId
     * @return
     */
    @PostMapping
    public Result doSeckill(@RequestParam Long userId, @RequestParam Long productId){
        return Result.success(seckillService.doSeckill(userId, productId));
    }

    @PostMapping("/{productId}")
    public String seckill(@RequestHeader("token") String token,
                          @PathVariable Long productId){

        Long userId = JwtUtil.parseToken(token);
        return seckillService.doSeckill1(userId, productId);
    }
}