package com.mall.controller;

import com.mall.po.dto.LoginDTO;
import com.mall.po.dto.UserRoleDTO;
import com.mall.po.entity.User;
import com.mall.po.vo.Result;
import com.mall.security.JwtUtil;
import com.mall.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.register(user);
        return Result.success("注册成功");
    }

    @PostMapping("/admin/kickout/{userId}")
    public Result kickOut(@PathVariable Long userId) {
        redisTemplate.delete("user:token:" + userId);
        return Result.success("用户已被踢出");
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token != null && !token.isBlank()) {
            String username = JwtUtil.getUsername(token);
            User user = userService.findByUsername(username);
            if (user != null) {
                redisTemplate.delete("user:token:" + user.getId());
            }
        }
        return Result.success("退出登录成功");
    }

    @PostMapping("/assignRole")
    @PreAuthorize("hasAuthority('USER_ASSIGN_ROLE')")
    public Result assignRole(@RequestBody UserRoleDTO dto) {
        userService.assignRole(dto);
        return Result.success("分配成功");
    }
}
