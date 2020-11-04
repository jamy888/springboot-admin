package com.eyoung.springbootadmin.app.controller;

import com.eyoung.springbootadmin.util.MD5Utils;
import com.eyoung.springbootadmin.weixin.mp.service.IAuthStatus;
import com.eyoung.springbootadmin.weixin.mp.service.IWeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
@RequestMapping("")
public class SysController {

    @Autowired
    private IWeixinService weixinService;

    @RequestMapping("/")
    public String root() {
        return "redirect:/pages/admin-index";
    }

    @RequestMapping("/admin-login")
    public String adminLogin(Model model, HttpServletRequest request) {
//        String token = MD5Utils.strTo16(System.currentTimeMillis() + UUID.randomUUID().toString());
//        model.addAttribute( "token", token);
        model.addAttribute( "token", request.getSession().getId());
        return "admin-login";
    }

    @RequestMapping("/admin-register")
    public String adminRegister() {
        return "admin-register";
    }

    @GetMapping("wechat-login")
    public String weChatLogin(Model model, HttpServletRequest request){
//        String token = MD5Utils.strTo16(System.currentTimeMillis() + UUID.randomUUID().toString());
        model.addAttribute( "token", request.getSession().getId());
        return "wechat-login";
    }

    @GetMapping("mobile-auth")
    public String mobileAuth(Model model, HttpServletRequest request){
//        String token = MD5Utils.strTo16(System.currentTimeMillis() + UUID.randomUUID().toString());
        String state = request.getParameter("state");

        model.addAttribute( "sessionId", state);
        weixinService.putSessionStatusToCache(state, IAuthStatus.SCANNED);
        return "mobile-auth";
    }

    @RequestMapping("/documentation")
    public String documentation() {
        return "pages/documentation";
    }

}
