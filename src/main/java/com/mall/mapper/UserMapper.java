package com.mall.mapper;

import com.mall.po.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);

    @Insert("insert into user(username,password) values(#{username},#{password})")
    void insert(User user);
}