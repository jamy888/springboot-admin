package com.eyoung.springbootadmin.security.core;

import com.eyoung.springbootadmin.security.service.MyUserDetailsService;
import com.eyoung.springbootadmin.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 登陆成功处理handler
 */
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
//        //登录成功返回
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("code", "200");
//        paramMap.put("message", "登录成功!");
//        //设置返回请求头
//        response.setContentType("application/json;charset=utf-8");
//        //写出流
//        PrintWriter out = response.getWriter();
//        out.write(JacksonUtil.toJson(paramMap));
//        out.flush();
//        out.close();

        String targetUrl = request.getParameter("targetUrl");
        if (targetUrl == null || "".equals(targetUrl)) {
            targetUrl = "/pages/admin-index";
        }
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        //获取授权后的用户信息
        String username = (String) authentication.getPrincipal();
        //更新用户信息
        myUserDetailsService.loadUserByUsername(username);
        //TODO 密码处理

        //发送邮件，推送消息，短信等等

        super.onAuthenticationSuccess(request,response,authentication);

    }

}