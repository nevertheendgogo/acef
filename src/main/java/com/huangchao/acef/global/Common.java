package com.huangchao.acef.global;


import com.huangchao.acef.utils.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

public class Common {

                                            //图片映射路径  文件存放根路径  文件存放目录
    public static void deletePreviousPicture(String url,String filePath,String imgPath) throws IOException {
        if (url != null) {
            int i = url.lastIndexOf("/") + 1;
            //获取图片名
            String fileName = url.substring(i);
            //打开文件流
            File file = new File(filePath + imgPath + fileName);
            //如果图片存在则删除
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
        return language;
    }
}
