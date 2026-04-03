package com.mall.service;

import java.util.List;

public interface PermissionService {
    List<String> getPermissions(String url, String method);
}
