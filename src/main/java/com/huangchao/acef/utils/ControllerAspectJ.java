package com.huangchao.acef.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class ControllerAspectJ {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAspectJ.class);
    private static Map<String, String> resultMap = new HashMap<>();

    @Autowired
    private SystemConfig systemConfig;
    static {
        resultMap.put("result", "0");
    }

    @Around("execution(* com.huangchao.acef.controller.*.*(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) {
        long startTime = System.currentTimeMillis();


        Object result = null;

        try {
            result = pjp.proceed();
            logger.info(pjp.getSignature() + "\tuse time:" + (System.currentTimeMillis() - startTime)+"ms");
        } catch (Throwable e) {
            logger.error("发生错误，原因如下：", e);
            //设置邮件内容
            String content = "<html><head></head><body><h3>系统运行发生错误，信息如下：</h3><h3>出错位置："+pjp.getSignature()+"</h3><h3>出错原因："+e+"</h3></body></html>";
            //发送邮件
            new EmailUtil(systemConfig.fromEmail, systemConfig.errorEmail, systemConfig.authorizationCode).run("法国东部华人协会(ACEF)", content);
            result = resultMap;
        }

        return result;
    }

}
