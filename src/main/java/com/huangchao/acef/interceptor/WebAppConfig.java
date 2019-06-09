package com.huangchao.acef.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    /**
     * 本类用于登录权限检查配置拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截所有请求，除了/user路径下的所有子请求
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/u*","/d*","/c*","/fb/g*","/gob","/gab").excludePathPatterns("/user/**");
    }
}
