package com.mall.mapper;

import com.mall.po.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    void insert(Role role);


    List<Role> selectAllWithPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countAll();

    List<Role> searchByNameWithPage(@Param("name") String name, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countByName(@Param("name") String name);
}
