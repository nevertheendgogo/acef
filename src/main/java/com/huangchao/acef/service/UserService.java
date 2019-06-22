package com.huangchao.acef.service;

import ch.qos.logback.classic.Logger;
import com.huangchao.acef.dao.UserMapper;
import com.huangchao.acef.entity.User;
import com.huangchao.acef.utils.CookieUtil;
import com.huangchao.acef.utils.EmailUtil;
import com.huangchao.acef.utils.IpUtils;
import com.huangchao.acef.utils.Md5;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 本类为用户数据相关操作
 */
@Service
public class UserService {

    //注入映射用户信息相关数据库操作类
    @Autowired
    private UserMapper mapper;
    //统一cookie存活基准时间
    @Value("${survivalTime}")
    private int survivalTime;
    @Value("${fromEmail}")
    String fromEmail;// 发件人电子邮箱
    @Value("${authorizationCode}")
    String authorizationCode;// 邮箱第三方登录授权码
    //常用ip地址
    private static Set<String> ipAddress = new HashSet<>();
    private final static Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);

    //查找用户信息
    public User findUserByEmailAccount(String emailAccount) {
        return mapper.findUserByEmailAccount(emailAccount);
    }

    //查找用户id
    public String findIdByEmailAccount(String emailAccount) {
        return mapper.findIdByEmailAccount(emailAccount);
    }

    //查找用户信息
    public User findUserById(String id) {
        return mapper.findUserById(id);
    }

    //登录状态查询
    public Map<String, String> isLogin(HttpServletRequest request) {
        //用于返回查询结果,成功则返回1和昵称
        Map<String, String> result = new HashMap<>();

//        logger.error("isLogin:\n");
        if (ipAddress.contains(IpUtils.getIpAddr(request))) {

            //获取从客户端携带过来的cookie
            Cookie[] cookies = request.getCookies();
            //从cookie的数组中查找指定名称的cookie
            Cookie cookie = CookieUtil.findCookie(cookies, "loginUserId");

//            logger.error("ip地址：" + IpUtils.getIpAddr(request)+
//                    "\ncookie里面的loginUserId："+cookie.getValue()+
//                    "\nSession里面的loginUserId:"+loginUserId+"\n\n");

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

                }
//                else {
//                    //七天自动登录代码实现
//                    //若为空，则在数据库查询，查询成功后保存到本次会话session中
//                    User user = findUserById(cookie.getValue());
//                    if (user.getId() != null) {
//                        request.getSession().setAttribute("loginUserId", user.getId());
//                        result.put("result", "1");
//                    } else
                result.put("result", "0");
//                }

            } else {
                //若cookie为空则是未登录
                result.put("result", "0");
            }
        } else  //非常用ip地址
            result.put("result", "2");

        return result;
    }

    //管理员登陆
    public Map<String, String> login(User user, HttpServletRequest request, HttpServletResponse response) {
        //用于返回登录结果
        Map<String, String> result = new HashMap<>();

        //判断是否是常用ip地址登录
        if (ipAddress.contains(IpUtils.getIpAddr(request))) {

            if (request.getSession().getAttribute("times") == null || (int) request.getSession().getAttribute("times") > 0) {

                //检查用户是否存在
                //从数据库获取登录用户账户信息
                User userFinded;
                if (user.getEmailAccount() != null && user.getPassword() != null && !user.getEmailAccount().equals("") && !user.getPassword().equals(""))
                    userFinded = findUserByEmailAccount(user.getEmailAccount());
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
                        String id = findIdByEmailAccount(user.getEmailAccount());
                        //本次会话保存登录状态
                        request.getSession().setAttribute("loginUserId", id);
                        //存储cookie,完成记住用户登录状态功能:
                        CookieUtil.saveCookie("loginUserId", id, response, survivalTime);
                        //登录成功后把可登录次数重置
                        request.getSession().setAttribute("times", 5);
                        //将当前ip地址存入常用ip地址
                        ipAddress.add(IpUtils.getIpAddr(request));
                        logger.error("login:\nip地址：" + IpUtils.getIpAddr(request));
                        //最多允许存储3个常用IP地址
                        if (ipAddress.size() >= 4) {
                            ipAddress.remove(ipAddress.toArray()[0]);
                        }

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

            } else {
                //超过登陆次数
                result.put("result", "5");
                //半小时后登陆
                request.getSession().setMaxInactiveInterval(1800);
            }
        } else {
            //非常用ip地址登录
            //进行邮箱身份验证
            //设置邮件内容
            //生成验证码
            int code = (int) (Math.random() * 10000);
            //确保四位
            if (code < 1000)
                code += 1000;
            //保存验证码到session
            request.getSession().setAttribute("code", code + "");
            String content = "<html><head></head><body><h1>请点击如下链接验证身份，验证成功后可返回登录界面登录</h1><h3>http://huangchaoweb.cn/acef/user/sui/"
                    + IpUtils.getIpAddr(request) + "/" + code + "</h3></body></html>";
            new EmailUtil(fromEmail, user.getEmailAccount(), code + "", authorizationCode).run("首次登录身份验证", content);
            result.put("result", "6");

        }

        return result;
    }

    //发送验证码
    public Map<String, String> sendCode(String emailAccount, HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();

        if (request.getSession().getAttribute("emailAccount") == null) {

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
            request.getSession().setAttribute("times", 3);
            //有效时间为3分钟
            request.getSession().setMaxInactiveInterval(180);

            //保存发送结果
            result.put("result", result1 + "");
        }else//上次验证码时间未过
            result.put("result", "2");

        return result;
    }

    //修改密码
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
                //判断修改次数是否超过3次
                if ((int) request.getSession().getAttribute("times") > 0) {

                    //判断验证码是否正确
                    if ((code + "").equals(request.getSession().getAttribute(emailAccount) + "")) {
                        //验证码正确
                        //执行修改密码操作
                        if (mapper.changePassword(emailAccount, Md5.encode(password)) == 0) {
                            result.put("result", "2");
                            return result;
                        } else {
                            result.put("result", "1");
                            return result;
                        }

                    } else {

                        //验证码不正确,半小时内允许修改次数-1
                        if (request.getSession().getAttribute("times") == null || (int) request.getSession().getAttribute("times") > 0) {
                            request.getSession().setAttribute("times", (int) request.getSession().getAttribute("times") - 1);
                        }

                        result.put("result", "3");
                        return result;
                    }

                } else {
                    result.put("result", "4");
                    //30分钟后可再次发送验证码并修改密码
                    request.getSession().setMaxInactiveInterval(80);
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

    //设置常用IP地址
    public String setCommonIp(String ipAddr,String code,HttpServletRequest request) {

        logger.error("setCommonIp:\nip地址：" + ipAddr);
        if (code!=null&&request.getSession().getAttribute("code")!=null){
            if (code.equals(request.getSession().getAttribute("code"))){
                //将当前ip地址存入常用ip地址
                ipAddress.add(ipAddr);
                //最多允许存储3个常用IP地址
                if (ipAddress.size() >= 4) {
                    ipAddress.remove(ipAddress.toArray()[0]);
                }
                //清除验证码信息
                request.getSession().setAttribute("code",null);
                return "验证成功，可返回登录界面登录";
            }
        }else
            //非法破解，清除所有信息，保留黑名单实现
            request.getSession().setMaxInactiveInterval(0);
        return "别试了，成功的几率很小，哈哈哈";

    }


}
