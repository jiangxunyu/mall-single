package com.mall.mapper;

import com.mall.po.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {
    List<String> getByUserId(Long userId);

    List<String> getByUrlAndMethod(@Param("url") String url, @Param("method") String method);

    List<String> getRoleCodesByUserId(Long userId);

    List<String> getAllCodes();

    void insert(Permission permission);

    List<Permission> list();

    List<Permission> selectAllWithPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countAll();

    List<Permission> searchByNameWithPage(@Param("name") String name, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countByName(@Param("name") String name);
}
