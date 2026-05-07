package com.mall.service.impl;

import com.mall.mapper.PermissionMapper;
import com.mall.po.entity.Permission;
import com.mall.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<String, Object> listPermissions(String keyword, Integer pageNum, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        int offset = (pageNum - 1) * pageSize;
        if (keyword == null || keyword.isBlank()) {
            result.put("list", permissionMapper.selectAllWithPage(offset, pageSize));
            result.put("total", permissionMapper.countAll());
        } else {
            result.put("list", permissionMapper.searchByNameWithPage(keyword, offset, pageSize));
            result.put("total", permissionMapper.countByName(keyword));
        }
        return result;
    }
}
