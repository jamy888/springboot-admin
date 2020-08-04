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

    @GetMapping("admin-login")
    public String sysLogin(Model model, HttpServletRequest request){
        String sessionId = request.getParameter("sessionId");
        model.addAttribute( "sessionId", sessionId);
        return "admin-login";
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

//    @GetMapping("admin-404")
//    public String sys404(){
//        return "pages/admin-404";
//    }

//    @RequestMapping("/admin-404")
//    public ModelAndView index(HttpServletRequest request) {
//        ModelAndView modelAndView = new ModelAndView("pages/index");
//
//        String page = request.getParameter("page");
//        String content = request.getParameter("content");
//        // 不带参数请求
//        if (page == null || page.equals("") || content == null || content.equals("")) {
//            page = "test";
//            content = "test-content";
//        }
//        modelAndView.addObject("page", "pages/admin-404");
//        modelAndView.addObject("content", "admin-404");
//        return modelAndView;
//    }

    @GetMapping("admin-500")
    public String sys500(){
        return "pages/admin-500";
    }

    @GetMapping("admin-blank")
    public String sysBlank(){
        return "pages/admin-blank";
    }

    @GetMapping("admin-index")
    public String index(){
        return "pages/admin-index";
    }
}
