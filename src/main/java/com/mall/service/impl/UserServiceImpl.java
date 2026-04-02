package com.mall.service.impl;

import com.mall.entity.LoginDTO;
import com.mall.entity.User;
import com.mall.mapper.UserMapper;
import com.mall.security.JwtUtil;
import com.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(LoginDTO dto){
        User user = userMapper.findByUsername(dto.getUsername());

        if(user == null || !user.getPassword().equals(dto.getPassword())){
            throw new RuntimeException("账号密码错误");
        }

        return jwtUtil.generateToken(user.getId());
    }

    @Override
    public void register(User user) {
        userMapper.insert(user);
    }
}