package com.huangchao.acef.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 本类为跳转游客可访问的页面
 */

@Controller
@RequestMapping("/test")
public class TestOperation {


    //    跳转相应请求界面
    @RequestMapping("/to/{name}")
    public String toAllPage(@PathVariable String name) {
        return "../" + name;
    }


}

