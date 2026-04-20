package com.mall.controller;

import com.mall.po.entity.User;
import com.mall.po.vo.Result;
import com.mall.security.JwtUtil;
import com.mall.service.OrderService;
import com.mall.service.UserService;
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

    @PostMapping("/create")
    public Result create(HttpServletRequest request, @RequestParam Long productId, @RequestParam Integer count) {
        Long userId = extractUserId(request.getHeader("token"));
        Long orderId = orderService.createOrder(userId, productId, count);
        return Result.success(orderId);
    }

    /**
     * 根据购物车创建订单
     */
    @PostMapping("/createByCart")
    public Result createByCart(HttpServletRequest request) {
        Long userId = extractUserId(request.getHeader("token"));
        return Result.success(orderService.createByCart(userId));
    }
}
