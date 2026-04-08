package com.mall.service;

import java.security.Permission;
import java.util.List;

public interface PermissionService {
    List<String> getPermissions(String url, String method);

    void addPermission(Permission p);

    List<Permission> list();

}
