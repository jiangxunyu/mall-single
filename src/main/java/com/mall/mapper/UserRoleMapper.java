package com.mall.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleMapper {
    void deleteByUserId(Long userId);

    void insert(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
