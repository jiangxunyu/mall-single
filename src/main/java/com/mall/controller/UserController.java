package com.mall.controller;

import com.mall.entity.LoginDTO;
import com.mall.entity.Result;
import com.mall.entity.User;
import com.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO dto){
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user){
        userService.register(user);
        return Result.success("注册成功");
    }
}