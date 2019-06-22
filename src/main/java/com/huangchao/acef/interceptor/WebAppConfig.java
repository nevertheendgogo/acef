package com.huangchao.acef.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private CharacterInterceptor characterInterceptor;
    /**
     * 本方法用于登录权限检查配置拦截器和字符编码过滤
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截所有请求，除了/user路径下的所有子请求
        registry.addInterceptor(loginInterceptor).addPathPatterns("/*/u*","/*/d*","/*/c*","/fb/g*","/mi/gob","/mi/gab").excludePathPatterns("/user/**","/fb/u");
        //拦截所有请求，进行字符过滤
        registry.addInterceptor(characterInterceptor).addPathPatterns("/**");

    }

}
