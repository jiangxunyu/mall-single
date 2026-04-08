package com.mall.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {
    void deleteByUserId(Long userId);

    void insert(Long userId, Long roleId);
}
