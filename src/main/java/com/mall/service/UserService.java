package com.mall.service;


import com.mall.entity.LoginDTO;
import com.mall.entity.User;

public interface UserService {
    String login(LoginDTO dto);

    void register(User user);

    User findByUsername(String username);
}