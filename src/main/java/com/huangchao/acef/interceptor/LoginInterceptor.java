package com.huangchao.acef.interceptor;

import ch.qos.logback.classic.Logger;
import com.huangchao.acef.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 本类未检查所有需要登陆权限的请求是否意见登陆
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

    //获取业务层操作类
    @Autowired
    private UserService userService;
    private final static Logger logger = (Logger) LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String name=handler.toString().substring(0,handler.toString().indexOf("("));
        logger.info(name.substring(name.lastIndexOf(".")+1));

        //已登录则放行
        if (userService.isLogin(request).get("result").equals("1")){
        logger.info("放行");
            return true;
        }
        logger.info("不放行");
        return false;
    }
}
