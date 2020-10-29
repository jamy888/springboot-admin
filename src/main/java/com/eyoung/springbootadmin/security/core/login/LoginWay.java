package com.eyoung.springbootadmin.security.core.login;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/10/28 17:37
 */
public abstract class LoginWay {

    public abstract UserDetails loadUserByUsername(String sessionId, String username) throws UsernameNotFoundException;
}
