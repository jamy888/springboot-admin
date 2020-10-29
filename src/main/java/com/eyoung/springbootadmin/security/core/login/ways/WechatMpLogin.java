package com.eyoung.springbootadmin.security.core.login.ways;

import com.eyoung.springbootadmin.security.core.login.ChooseWay;
import com.eyoung.springbootadmin.security.core.login.LoginWay;
import com.eyoung.springbootadmin.security.service.MyUserDetailsService;
import com.eyoung.springbootadmin.weixin.mp.service.IWeixinService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/10/28 17:59
 */
@ChooseWay(value = "WechatMp")
@Component
@Slf4j
public class WechatMpLogin extends LoginWay {

    @Autowired
    private MyUserDetailsService userService;
    @Autowired
    private IWeixinService weixinService;

    @Override
    public UserDetails loadUserByUsername(String sessionId, String username) throws UsernameNotFoundException {
        WxMpUser wxMpUser = weixinService.getWxMpUserFromCache(sessionId);
        if (wxMpUser == null) {
            log.error("sessionId = {} 已经过期，请刷新页面再试一次", sessionId);
            throw new UsernameNotFoundException("sessionId=" + sessionId + "已经过期，请刷新页面再试一次");
        }
        String openId = wxMpUser.getOpenId();
        UserDetails user = userService.loadUserByOpenId(openId);
        if (user == null) {
            log.error("session = {}, openId={}对应的账号不存在，请联系管理员", sessionId, openId);
            throw new UsernameNotFoundException("微信" + wxMpUser.getNickname() + "对应的账号不存在，请联系管理员");
        }
        return user;
    }

}
