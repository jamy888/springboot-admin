package com.eyoung.springbootadmin.security.mapper;

import com.eyoung.springbootadmin.security.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select( "select id , username , password, enabled, accountExpireAt, passwordExpireAt, hasLocked, openId from user where username = #{username}" )
    User loadUserByUsername(@Param("username") String username);

    @Select( "select id , username , password, enabled, accountExpireAt, passwordExpireAt, hasLocked, openId from user where openId = #{openId}" )
    User loadUserByOpenId(@Param("openId") String openId);

}
