package com.eyoung.springbootadmin.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController extends BaseController {

    @RequestMapping("/")
    public String root() {
        return "redirect:/pages/admin-index";
    }

//    @RequestMapping("/index")
//    public String index() {
//        return "pages/index";
//    }

//    @RequestMapping("/index")
//    public ModelAndView index(HttpServletRequest request) {
//        ModelAndView modelAndView = new ModelAndView(INDEX_PAGE);
//
//        String page = request.getParameter("page");
//        String content = request.getParameter("content");
//        // 不带参数请求
//        if (page == null || page.equals("") || content == null || content.equals("")) {
//            page = "test";
//            content = "test-content";
//        }
//        modelAndView.addObject("page", "pages/admin-index");
//        modelAndView.addObject("content", "admin-index");
//        return modelAndView;
//    }


    @RequestMapping("/admin-login")
    public String adminLogin() {
        return "admin-login";
    }

    @RequestMapping("/admin-register")
    public String adminRegister() {
        return "admin-register";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute( "loginError"  , true);
        return "login";
    }

    @GetMapping("/401")
    public String accessDenied() {
        return "401";
    }

    @GetMapping("/user/common")
    public String common() {
        return "user/common";
    }

    @GetMapping("/user/admin")
    public String admin() {
        return "user/admin";
    }


}
