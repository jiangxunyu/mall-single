package com.mall.service;

import com.mall.po.dto.RolePermissionDTO;
import com.mall.po.entity.Role;

public interface RoleService {
    void addRole(Role role);

    void assignPermission(RolePermissionDTO dto);
}
