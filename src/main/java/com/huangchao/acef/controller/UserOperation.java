package com.huangchao.acef.controller;

import com.huangchao.acef.entity.*;
import com.huangchao.acef.service.UserService;
import com.huangchao.acef.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 本类为管理员登陆权限处理类
 */

@Controller
@RequestMapping("/user")
public class UserOperation {

    //获取业务层操作类
    @Autowired
    private UserService userService;

    //登录状态查询
    @RequestMapping(value = "/isLogin", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> isLogin(HttpServletRequest request) {
        return userService.isLogin(request);
    }

    //管理员登陆
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(User user, HttpServletRequest request, HttpServletResponse response) {
        return userService.login(user,request,response);
    }

    /****************修改密码**************/

    //发送验证码
    @RequestMapping(value = "/sc", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> sendCode(String emailAccount, HttpServletRequest request) {
        return userService.sendCode(emailAccount,request);
    }

    //修改密码
    @RequestMapping(value = "/cp", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, String> changePassword(String emailAccount, int code, String password, HttpServletRequest request) {
        return userService.changePassword(emailAccount,code,password,request);

    }

    //设置常用ip地址
    @RequestMapping(value = "/sui/{ipAddr}/{code}",produces= MediaType.TEXT_HTML_VALUE+";charset=utf-8")
    @ResponseBody
    public String setCommonIp(@PathVariable("ipAddr") String ipAddr, @PathVariable("code") String code, HttpServletRequest request) {
        return userService.setCommonIp(ipAddr, code, request);
    }

    @RequestMapping("/gip")
    @ResponseBody
    public static String getIp2(HttpServletRequest request) {
        return IpUtils.getIpAddr(request);
    }

}

