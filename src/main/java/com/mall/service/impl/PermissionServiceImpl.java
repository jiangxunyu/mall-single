package com.mall.service.impl;

import com.mall.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Override
    public List<String> getPermissions(String url, String method) {
        return List.of();
    }
}
