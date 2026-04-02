package com.mall.controller;

import com.mall.entity.Result;
import com.mall.security.JwtUtil;
import com.mall.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result add(HttpServletRequest request,
                      Long productId,
                      Integer count){
        
        String token = request.getHeader("token");
        Long userId = JwtUtil.parseToken(token);
        cartService.add(userId, productId, count);

        return Result.success("添加成功");
    }

    @GetMapping("/list")
    public Result list(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtil.parseToken(token);
        Map<Object, Object> list = cartService.list(userId);
        return Result.success(list);
    }

    @DeleteMapping("/remove")
    public Result remove(HttpServletRequest request,
                         Long productId){

        String token = request.getHeader("token");
        Long userId = JwtUtil.parseToken(token);
        cartService.remove(userId, productId);
        return Result.success("删除成功");
    }
}