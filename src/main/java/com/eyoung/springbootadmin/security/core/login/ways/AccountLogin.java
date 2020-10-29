package com.eyoung.springbootadmin.security.core.login.ways;

import com.eyoung.springbootadmin.security.core.login.ChooseWay;
import com.eyoung.springbootadmin.security.core.login.LoginWay;
import com.eyoung.springbootadmin.security.service.MyUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/10/28 17:58
 */
@ChooseWay(value = "Account")
@Component
@Slf4j
public class AccountLogin extends LoginWay {

    @Autowired
    private MyUserDetailsService userService;

    @Override
    public UserDetails loadUserByUsername(String sessionId, String username) throws UsernameNotFoundException {
        UserDetails user = userService.loadUserByUsername(username);
        if (user == null) {
            log.error("账户{}不存在，请联系管理员", username);
            throw new UsernameNotFoundException("账户" + username + "不存在，请联系管理员");
        }
        return user;
    }
}
