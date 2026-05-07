package com.mall.mapper;

import com.mall.po.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);

    @Insert("insert into user(username,password) values(#{username},#{password})")
    void insert(User user);


    List<User> selectAllWithPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countAll();

    List<User> searchByUsernameWithPage(@Param("username") String username, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countByUsername(@Param("username") String username);
}