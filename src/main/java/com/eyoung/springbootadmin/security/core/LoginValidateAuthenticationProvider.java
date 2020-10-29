package com.eyoung.springbootadmin.security.core;

import com.eyoung.springbootadmin.security.core.login.LoginFactory;
import com.eyoung.springbootadmin.security.core.login.LoginWay;
import com.eyoung.springbootadmin.security.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class LoginValidateAuthenticationProvider implements AuthenticationProvider {
    //解密用的
    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LoginFactory loginFactory;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails user = new User();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 获取sessionId，理论上该值与request.getParameter("sessionId")获取到的值一致
        String sessionId = request.getSession().getId();
        String type = request.getParameter("type");
        String username = (String) authentication.getPrincipal();
        String rawPassword = (String) authentication.getCredentials();

        LoginWay loginWay = loginFactory.getWay(type);
        user = loginWay.loadUserByUsername(sessionId, username);

        // 当前端传过来的登录方式为非账户登录，此时 authentication.getPrincipal() 得到的username值为""，因此，需要重新获取
        username = user.getUsername();
        if (!user.isEnabled()) {
            log.error("账户{}已被禁用，请联系管理员", username);
            throw new DisabledException("账户" + username + "已被禁用，请联系管理员");

        } else if (!user.isAccountNonLocked()) {
            log.error("账户{}已被锁定，请联系管理员", username);
            throw new LockedException("该账号已被锁定");
        } else if (!user.isAccountNonExpired()) {
            log.error("账户{}已过期，请联系管理员", username);
            throw new AccountExpiredException("账户" + username + "已过期，请联系管理员");

        } else if (!user.isCredentialsNonExpired()) {
            log.error("账户{}密码已过期，请联系管理员", username);
            throw new CredentialsExpiredException("账户" + username + "密码已过期，请重新登录");
        }

        //验证密码
        else if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("输入密码错误!");
        }
        return new UsernamePasswordAuthenticationToken(username, rawPassword, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //确保authentication能转成该类
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
