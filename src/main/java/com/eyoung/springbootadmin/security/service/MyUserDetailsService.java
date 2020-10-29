package com.eyoung.springbootadmin.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/10/28 15:48
 */
public interface MyUserDetailsService extends UserDetailsService {

    UserDetails loadUserByOpenId(String openId) throws UsernameNotFoundException;
}
