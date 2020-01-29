package com.huangchao.acef.utils;


import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

public class Common {

    private final static Logger logger = (Logger) LoggerFactory.getLogger(Common.class);

    //图片映射路径  文件存放根路径  文件存放目录
    public static void deletePreviousPicture(String url, String filePath, String imgPath) {

        if (url != null) {
            int i = url.lastIndexOf("/") + 1;
            //获取图片名
            String fileName = url.substring(i);
            //打开文件流
            logger.info("要删除图片的完整路径为：{}", filePath + imgPath + fileName);
            File file = new File(filePath + imgPath + fileName);
            //如果图片存在则删除
            logger.info("图片是否存在：{}", file.exists());
            if (file.exists())
                file.delete();
        }
    }

    //获取cookie中保存的语言
    public static String getLanguage(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String language = null;
        if (CookieUtil.findCookie(cookies, "language") != null) {
            language = CookieUtil.findCookie(cookies, "language").getValue();
        }
        logger.info("获取cookie中存储的语言：{}",language);
        return language;
    }
}
