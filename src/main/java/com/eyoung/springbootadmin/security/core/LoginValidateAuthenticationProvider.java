package com.eyoung.springbootadmin.security.core;

import com.eyoung.springbootadmin.security.entity.User;
import com.eyoung.springbootadmin.security.service.MyUserDetailsServiceImpl;
import com.eyoung.springbootadmin.weixin.mp.service.IWeixinService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
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

@Component
public class LoginValidateAuthenticationProvider implements AuthenticationProvider {
    @Resource
    private MyUserDetailsServiceImpl userService;
    @Autowired
    private IWeixinService weixinService;
    //解密用的
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails user = new User();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String sessionId = request.getSession().getId();
        String rawPassword = (String) authentication.getCredentials();
        if (StringUtils.isNotBlank(sessionId)){
            WxMpUser wxMpUser = weixinService.getWxMpUserFromCache(sessionId);
            if (wxMpUser != null){
                String openId = wxMpUser.getOpenId();
                user = userService.loadUserByOpenId(openId);
                rawPassword = user.getPassword();
            }
        }else{
            //获取输入的用户名
            String username = authentication.getName();
            //获取输入的明文
            //查询用户是否存在
            user = userService.loadUserByUsername(username);
            //验证密码
            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                throw new BadCredentialsException("输入密码错误!");
            }
        }

        if (!user.isEnabled()) {
            throw new DisabledException("该账户已被禁用，请联系管理员");

        } else if (!user.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定");

        } else if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期，请联系管理员");

        } else if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("该账户的登录凭证已过期，请重新登录");
        }
        return new UsernamePasswordAuthenticationToken(user, rawPassword, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //确保authentication能转成该类
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
