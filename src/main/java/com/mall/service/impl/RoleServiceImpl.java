package com.mall.service.impl;

import com.mall.mapper.RolePermissionMapper;
import com.mall.po.dto.RolePermissionDTO;
import com.mall.po.entity.Role;
import com.mall.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public void addRole(Role role) {

    }

    @Override
    public void assignPermission(RolePermissionDTO dto) {
        rolePermissionMapper.deleteByRoleId(dto.getRoleId());

        for (Long pid : dto.getPermissionIds()) {
            rolePermissionMapper.insert(dto.getRoleId(), pid);
        }
    }
}
