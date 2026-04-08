package com.mall.service;


import com.mall.po.dto.LoginDTO;
import com.mall.po.dto.UserRoleDTO;
import com.mall.po.entity.User;

import java.util.List;

public interface UserService {
    String login(LoginDTO dto);

    void register(User user);

    User findByUsername(String username);

    void assignRole(UserRoleDTO dto);

    List<String> getUserPermissions(Long userId);
}