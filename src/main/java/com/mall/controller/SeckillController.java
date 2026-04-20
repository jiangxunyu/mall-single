package com.mall.controller;

import com.mall.po.entity.User;
import com.mall.po.vo.Result;
import com.mall.security.JwtUtil;
import com.mall.service.SeckillService;
import com.mall.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private UserService userService;

    private Long extractUserId(String token) {
        String username = JwtUtil.getUsername(token);
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getId();
    }

    /**
     * 秒杀逻辑
     */
    @PostMapping("/{productId}")
    public Result doSeckill(HttpServletRequest request, @PathVariable Long productId) {
        Long userId = extractUserId(request.getHeader("token"));
        return Result.success(seckillService.doSeckill(userId, productId));
    }

    @PostMapping("/addStock/{productId}/{stock}")
    public Result addStock(@PathVariable Long productId, @PathVariable Long stock) {
        String key = "seckill:stock:" + productId;
        seckillService.addStock(key, stock);
        return Result.success("库存添加成功");
    }
}
