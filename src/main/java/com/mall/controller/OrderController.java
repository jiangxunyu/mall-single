package com.mall.controller;

import com.mall.entity.Result;
import com.mall.security.JwtUtil;
import com.mall.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result create(HttpServletRequest request, @RequestParam Long productId, @RequestParam Integer count){

        String token = request.getHeader("token");
        Long userId = JwtUtil.parseToken(token);
        Long orderId = orderService.createOrder(userId, productId, count);
        return Result.success(orderId);
    }

    /**
     * 根据购物车创建订单
     * @param request
     * @return
     */
    @PostMapping("/createByCart")
    public Result createByCart(HttpServletRequest request){

        String token = request.getHeader("token");
        Long userId = JwtUtil.parseToken(token);
        return Result.success(orderService.createByCart(userId));
    }
}