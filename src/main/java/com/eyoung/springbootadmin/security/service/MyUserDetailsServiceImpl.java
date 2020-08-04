package com.eyoung.springbootadmin.security.service;

import com.eyoung.springbootadmin.security.entity.Role;
import com.eyoung.springbootadmin.security.entity.User;
import com.eyoung.springbootadmin.security.mapper.RoleMapper;
import com.eyoung.springbootadmin.security.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 配置认证授权组件
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //查数据库
        User user = userMapper.loadUserByUsername( userName );
        if (null != user) {
            List<Role> roles = roleMapper.getRolesByUserId( user.getId() );
            user.setAuthorities( roles );
//            UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), roles);
        } else {
            throw new UsernameNotFoundException("不存在该用户!");
        }
        return user;
    }


    public UserDetails loadUserByOpenId(String openId) throws UsernameNotFoundException {
        //查数据库
        User user = userMapper.loadUserByOpenId(openId);
        if (null != user) {
            List<Role> roles = roleMapper.getRolesByUserId( user.getId() );
            user.setAuthorities( roles );
//            UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), roles);
        } else {
            throw new UsernameNotFoundException("不存在该用户!");
        }
        return user;
    }


}
