package com.mall.service.impl;

import com.mall.mapper.PermissionMapper;
import com.mall.mapper.UserMapper;
import com.mall.mapper.UserRoleMapper;
import com.mall.po.dto.LoginDTO;
import com.mall.po.dto.UserRoleDTO;
import com.mall.po.entity.User;
import com.mall.security.JwtUtil;
import com.mall.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public String login(LoginDTO dto) {
        // 这一步会走 loadUserByUsername，验证用户是否存在、密码是否正确（Spring Security 自动完成）
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userMapper.findByUsername(dto.getUsername());
        String token = JwtUtil.generateTokenByName(user.getUsername());

        // Redis 控制单端登录
        String key = "user:token:" + user.getId();
        // 旧 token（如果存在）
        String oldToken = redisTemplate.opsForValue().get(key);
        if (oldToken != null && !oldToken.isBlank()) {
            Claims claims = JwtUtil.getClaims(oldToken);
            long remainMs = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (remainMs > 0) {
                // 可选：加入黑名单，保证旧 token 立即失效
                redisTemplate.opsForValue().set("token:blacklist:" + oldToken, "1", remainMs, TimeUnit.MILLISECONDS);
            }
        }

        // 存新 token（覆盖旧的 = 踢人）
        redisTemplate.opsForValue().set(key, token, 30, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public void register(User user) {
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        userMapper.insert(user);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    @Transactional
    public void assignRole(UserRoleDTO dto) {
        // 先删旧关系
        userRoleMapper.deleteByUserId(dto.getUserId());
        // 批量插入
        for (Long roleId : dto.getRoleIds()) {
            userRoleMapper.insert(dto.getUserId(), roleId);
        }
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return permissionMapper.getByUserId(userId);
    }
}
