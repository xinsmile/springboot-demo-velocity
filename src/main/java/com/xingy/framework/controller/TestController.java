package com.xingy.framework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/** 
 * @author: xinguiyuan
 * @date: 2018-10-17 22:57:36 
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/index")
    public String index(ModelMap modelMap) {
        modelMap.addAttribute("name", "测试");
        return "index";
    }
}

