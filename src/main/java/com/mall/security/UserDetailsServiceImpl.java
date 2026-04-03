package com.mall.security;

import com.mall.entity.User;
import com.mall.mapper.PermissionMapper;
import com.mall.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. 查用户
        User user = userMapper.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 2. 查权限（RBAC）
        List<String> permissions = permissionMapper.getByUserId(user.getId());

        // 3. 转换成 Spring Security 需要的格式
        List<GrantedAuthority> authorities =
                permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 4. 返回 UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}