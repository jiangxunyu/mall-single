package com.mall.service.impl;

import com.mall.mapper.PermissionMapper;
import com.mall.po.entity.Permission;
import com.mall.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<String> getPermissions(String url, String method) {
        return permissionMapper.getByUrlAndMethod(url, method);
    }

    @Override
    public void addPermission(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    public List<Permission> list() {
        return permissionMapper.list();
    }
}
