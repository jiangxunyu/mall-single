package com.mall.service.impl;

import com.mall.service.PermissionService;
import org.springframework.stereotype.Service;

import java.security.Permission;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Override
    public List<String> getPermissions(String url, String method) {
        return List.of();
    }

    @Override
    public void addPermission(Permission p) {

    }

    @Override
    public List<Permission> list() {
        return List.of();
    }
}
