package com.mall.controller;

import com.mall.po.entity.User;
import com.mall.po.vo.Result;
import com.mall.security.JwtUtil;
import com.mall.service.CartService;
import com.mall.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

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

    @PostMapping("/add")
    public Result add(HttpServletRequest request, Long productId, Integer count) {
        Long userId = extractUserId(request.getHeader("token"));
        cartService.add(userId, productId, count);
        return Result.success("添加成功");
    }

    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        Long userId = extractUserId(request.getHeader("token"));
        Map<Object, Object> list = cartService.list(userId);
        return Result.success(list);
    }

    @DeleteMapping("/remove")
    public Result remove(HttpServletRequest request, Long productId) {
        Long userId = extractUserId(request.getHeader("token"));
        cartService.remove(userId, productId);
        return Result.success("删除成功");
    }
}
