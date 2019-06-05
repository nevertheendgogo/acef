package com.huangchao.acef.controller;

import com.huangchao.acef.entity.*;
import com.huangchao.acef.service.UserService;
import com.huangchao.acef.utils.CookieUtil;
import com.huangchao.acef.utils.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
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
    //统一cookie存活基准时间
    @Value("${survivalTime}")
    private int survivalTime;


    //登录状态查询
    @RequestMapping(value = "/isLogin",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> isLogin(HttpServletRequest request) {
        //用于返回查询结果,成功则返回1和昵称
        Map<String, String> result = new HashMap<>();

        //获取从客户端携带过来的cookie
        Cookie[] cookies = request.getCookies();
        //从cookie的数组中查找指定名称的cookie
        Cookie cookie = CookieUtil.findCookie(cookies, "loginUserId");
        //若存在
        if (cookie != null) {
            //如果服务器本次会话保存有登陆状态，判断loginUserId是否和cookie中的id一致
            String loginUserId = (String) request.getSession().getAttribute("loginUserId");
            if (loginUserId != null) {
                //若一致则cookie登陆有效
                if (loginUserId.equals(cookie.getValue()))
                    result.put("result", "1");
                else
                    result.put("result", "0");
            } else {
                //若为空，则在数据库查询，查询成功后保存到本次会话session中
                User user = userService.findUserById(cookie.getValue());
                if (user.getId() != null) {
                    request.getSession().setAttribute("loginUserId", user.getId());
                    result.put("result", "1");
                } else
                    result.put("result", "0");
            }
        } else {
            //若cookie为空则是未登录
            result.put("result", "0");
        }

        return result;
    }

    //管理员登陆
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(User user, HttpServletRequest request, HttpServletResponse response) {
        //用于返回登录结果
        Map<String, String> result = new HashMap<>();

        //检查用户是否存在
        //从数据库获取登录用户账户信息
        User userFinded;
        if (user.getEmailAccount() != null && user.getPassword() != null && !user.getEmailAccount().equals("") && !user.getPassword().equals(""))
            userFinded = userService.findUserByEmailAccount(user.getEmailAccount());
        else {
            result.put("result", "4");
            return result;
        }
        if (userFinded != null) {
            //判断密码是否正确
            if (userFinded.getPassword() != null && Md5.encode(user.getPassword()).equals(userFinded.getPassword())) {
                //身份确认
                result.put("result", "1");
                //获取管理员id
                String id = userService.findIdByEmailAccount(user.getEmailAccount());
                //本次会话保存登录状态
                request.getSession().setAttribute("loginUserId", id);
                //存储cookie,完成记住用户登录状态功能:
                CookieUtil.saveCookie("loginUserId", id, response, survivalTime);
                return result;
            } else {
                //密码错误
                result.put("result", "2");
                return result;
            }
        } else {
            //未找到该用户
            result.put("result", "3");
            return result;
        }
    }

}

