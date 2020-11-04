package com.eyoung.springbootadmin.app.controller;

import com.eyoung.springbootadmin.security.entity.User;
import com.eyoung.springbootadmin.security.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("pages")
public class PageController extends BaseController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 通用页面的action，例如admin-404,admin-500,admin-blank等等
     *
     * @param request
     * @param pageName
     * @return
     */
    @GetMapping("/{pageName}")
    public ModelAndView index(HttpServletRequest request, @PathVariable String pageName) {
        ModelAndView modelAndView = new ModelAndView(INDEX_PAGE);
        String page = "pages/" + pageName;
        String content = pageName;
        modelAndView.addObject("page", page);
        modelAndView.addObject("content", content);

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = userDetails.getUsername();
        User sysUser = userMapper.loadUserByUsername(username);
        // 脱敏
        sysUser.setPassword("");
        modelAndView.addObject("sysUser", sysUser);
        return modelAndView;
    }
}
