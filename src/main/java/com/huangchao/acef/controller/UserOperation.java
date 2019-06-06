package com.huangchao.acef.controller;

import com.huangchao.acef.entity.*;
import com.huangchao.acef.service.UserService;
import com.huangchao.acef.utils.CookieUtil;
import com.huangchao.acef.utils.EmailUtil;
import com.huangchao.acef.utils.IpUtils;
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
    @Value("${fromEmail}")
    String fromEmail;// 发件人电子邮箱
    @Value("${authorizationCode}")
    String authorizationCode;// 邮箱第三方登录授权码
    //常用ip地址
    private Set<String> ipAddress = new HashSet<>();

    //登录状态查询
    @RequestMapping(value = "/isLogin", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> isLogin(HttpServletRequest request) {
        //用于返回查询结果,成功则返回1和昵称
        Map<String, String> result = new HashMap<>();

        if (ipAddress.contains(IpUtils.getIpAddr(request))) {

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
        } else  //非常用ip地址
            result.put("result", "2");

        return result;
    }

    //管理员登陆
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(User user, HttpServletRequest request, HttpServletResponse response) {
        //用于返回登录结果
        Map<String, String> result = new HashMap<>();

        if (request.getSession().getAttribute("times")==null||(int) request.getSession().getAttribute("times") > 0) {

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
                    //将当前ip地址存入常用ip地址
                    ipAddress.add(IpUtils.getIpAddr(request));
                    return result;
                } else {
                    //密码错误
                    result.put("result", "2");
                    //设置
                    if (request.getSession().getAttribute("times") == null)
                        request.getSession().setAttribute("times", 4);
                    else
                        request.getSession().setAttribute("times", (int) request.getSession().getAttribute("times") - 1);

                    return result;
                }
            } else {
                //未找到该用户
                result.put("result", "3");

            }

        }else{
            //超过登陆次数
            result.put("result", "5");
            //半小时后登陆
            request.getSession().setMaxInactiveInterval(1800);
        }

        return result;
    }

    /****************修改密码**************/

    //发送验证码
    @RequestMapping(value = "/sc", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> sendCode(String emailAccount, HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();

        //生成验证码
        int code = (int) (Math.random() * 10000);
        //确保四位
        if (code < 1000)
            code += 1000;
        //保存验证码到session
        request.getSession().setAttribute("code", code + "");
        //保存当前发送验证码邮箱到session
        request.getSession().setAttribute("emailAccount", emailAccount);

        //发送验证码
        //设置邮件内容
        String content = "<html><head></head><body><h1>验证码如下</h1><h3>" + code + "</h3></body></html>";
        int result1 = new EmailUtil(fromEmail, emailAccount, code + "", authorizationCode).run("密码修改验证码", content);
        //保存当前时间戳到session
        request.getSession().setAttribute(emailAccount, code);
        //设置可以提交的次数
        request.getSession().setAttribute("times", "2");
        //有效时间为3分钟
        request.getSession().setMaxInactiveInterval(180);

        //保存发送结果
        result.put("result", result1 + "");
        return result;
    }

    //修改密码
    @RequestMapping(value = "/cp", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, String> changePassword(String emailAccount, int code, String password, HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();

        //判断密码是否为空
        if (password == null || password.equals("")) {
            result.put("result", "0");
            return result;
        }

        try {
            //判断服务器是否保存有验证码
            if (request.getSession().getAttribute(emailAccount) != null && !request.getSession().getAttribute(emailAccount).equals("")) {
                //判断修改次数是否超过两次
                System.out.println(request.getSession().getAttribute("times") + "666666");
                System.out.println(request.getSession().getAttribute("times").equals("2"));
                System.out.println(request.getSession().getAttribute("times").equals("1"));
                if (request.getSession().getAttribute("times").equals("2") || request.getSession().getAttribute("times").equals("1")) {

                    //判断验证码是否正确
                    if ((code + "").equals(request.getSession().getAttribute(emailAccount) + "")) {
                        //验证码正确
                        //执行修改密码操作
                        if (userService.changePassword(emailAccount, password) == 0) {
                            result.put("result", "2");
                            return result;
                        } else {
                            result.put("result", "1");
                            return result;
                        }

                    } else {

                        //验证码不正确
                        if (request.getSession().getAttribute("times") == null || request.getSession().getAttribute("times").equals("2")) {
                            request.getSession().setAttribute("times", "1");
                        } else {
                            request.getSession().setAttribute("times", "0");
                        }

                        result.put("result", "3");
                        return result;
                    }

                } else {
                    result.put("result", "4");
                    return result;
                }

            } else {
                result.put("result", "5");
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    @RequestMapping("/gip")
    @ResponseBody
    public static String getIp2(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

}

