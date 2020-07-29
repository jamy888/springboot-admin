package com.eyoung.springbootadmin.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("pages")
public class PageController extends BaseController {

    @GetMapping("/{pageName}")
    public ModelAndView index(HttpServletRequest request, @PathVariable String pageName) {
        ModelAndView modelAndView = new ModelAndView(INDEX_PAGE);
        String page = "pages/" + pageName;
        String content = pageName;
        modelAndView.addObject("page", page);
        modelAndView.addObject("content", content);
        return modelAndView;
    }
}
