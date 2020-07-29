package com.eyoung.springbootadmin.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class SysController {

    @GetMapping("admin-login")
    public String sysLogin(){
        return "admin-login";
    }

    @GetMapping("wechat-login")
    public String weChatLogin(){
        return "wechat-login";
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
