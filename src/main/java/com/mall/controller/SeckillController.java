package com.mall.controller;

import com.mall.po.vo.Result;
import com.mall.security.JwtUtil;
import com.mall.service.SeckillService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 秒杀逻辑
     * @param productId
     * @return
     */
    @PostMapping("/{productId}")
    public Result doSeckill(HttpServletRequest servletRequest,@PathVariable Long productId){
        String token = servletRequest.getHeader("token");
        Long userId = JwtUtil.parseToken(token);
        return Result.success(seckillService.doSeckill(userId, productId));
    }

    @PostMapping("/addStock/{productId}/{stock}")
    public Result addStock(@PathVariable Long productId, @PathVariable Long stock){
        String key = "seckill:stock:" + productId;
        seckillService.addStock(key, stock);
        return Result.success("库存添加成功");
    }

}