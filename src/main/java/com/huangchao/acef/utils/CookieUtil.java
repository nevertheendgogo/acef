package com.huangchao.acef.utils;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 本类为cookie工具类
 */
public class CookieUtil {
    private final static Logger logger = (Logger) LoggerFactory.getLogger(CookieUtil.class);

    public static Cookie findCookie(Cookie[] cookies, String name) {
        logger.info("查找名字为<{}>的cookie", name);
        if (cookies == null) {
            //说明客户端没有携带cookie
            return null;
        } else {
            //客户端携带了cookie
            for (Cookie cookie : cookies) {
                //将传来的cookie名与所有存储的cookie名对比，相同则选取该cookie返回
                if (name.equals(cookie.getName())) {
                    logger.info("查询结果为：{}", cookie.getValue());
                    return cookie;
                }
            }
        }
        return null;
    }

    public static void saveCookie(String name, String value, HttpServletResponse response, int rememberLoginTime) {
        logger.info("设置cookie，名为：{},值为：{}", name, value);
        //存储cookie,完成记住用户登录状态功能:
        Cookie cookie = new Cookie(name, value);
        // 设置有效时间:
        cookie.setMaxAge(rememberLoginTime);
        //我们设置Cookie的path为根目录"/"，以便在该域的所有路径下都能看到这个Cookie。
        cookie.setPath("/");
        //cookie.setPath(request.getContextPath() + "/");
        //将cookie添加到响应
        response.addCookie(cookie);
    }
}
