package com.mall.controller;

import com.mall.po.entity.User;
import com.mall.po.vo.Result;
import com.mall.security.JwtUtil;
import com.mall.service.OrderService;
import com.mall.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public Result list(HttpServletRequest request,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = extractUserId(request.getHeader("token"));
        Map<String, Object> result = orderService.listOrdersByPage(userId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Result detail(@PathVariable Long orderId) {
        Map<String, Object> order = orderService.getOrderDetail(orderId);
        return Result.success(order);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public Result cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return Result.success("订单取消成功");
    }
}
