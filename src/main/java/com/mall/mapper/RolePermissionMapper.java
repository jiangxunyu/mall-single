package com.mall.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolePermissionMapper {
    void deleteByRoleId(Long roleId);

    void insert(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
