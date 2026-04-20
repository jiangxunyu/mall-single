package com.mall.service;

import com.mall.po.entity.Permission;

import java.util.List;

public interface PermissionService {
    List<String> getPermissions(String url, String method);

    void addPermission(Permission permission);

    List<Permission> list();
}
