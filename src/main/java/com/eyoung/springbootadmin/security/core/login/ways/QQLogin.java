package com.eyoung.springbootadmin.security.core.login.ways;

import com.eyoung.springbootadmin.security.core.login.ChooseWay;
import com.eyoung.springbootadmin.security.core.login.LoginWay;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/10/28 18:00
 */
@ChooseWay(value = "qq")
@Component
public class QQLogin extends LoginWay {
    @Override
    public UserDetails loadUserByUsername(String sessionId, String username) throws UsernameNotFoundException {
        return null;
    }
}
