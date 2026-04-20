package com.mall.security;

import com.mall.mapper.PermissionMapper;
import com.mall.mapper.UserMapper;
import com.mall.po.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String SUPER_ADMIN = "SUPER_ADMIN";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        List<String> roleCodes = permissionMapper.getRoleCodesByUserId(user.getId());
        boolean isAdmin = roleCodes.stream().anyMatch(code ->
                SUPER_ADMIN.equals(code) || ROLE_ADMIN.equals(code));

        List<String> permissionCodes = isAdmin
                ? permissionMapper.getAllCodes()
                : permissionMapper.getByUserId(user.getId());

        List<String> authoritiesSource = new ArrayList<>(permissionCodes);
        authoritiesSource.addAll(roleCodes);

        List<GrantedAuthority> authorities = authoritiesSource.stream()
                .filter(code -> code != null && !code.isBlank())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
