package com.mall.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RolePermissionMapper {
    void deleteByRoleId(Long roleId);

    void insert(Long roleId, Long pid);
}
