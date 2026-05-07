package com.mall.service;

import com.mall.po.dto.RolePermissionDTO;
import com.mall.po.entity.Role;

import java.util.Map;

public interface RoleService {
    void addRole(Role role);

    void assignPermission(RolePermissionDTO dto);

    Map<String, Object> listRoles(String keyword, Integer pageNum, Integer pageSize);
}
