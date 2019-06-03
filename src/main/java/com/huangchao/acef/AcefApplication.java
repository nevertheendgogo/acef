package com.huangchao.acef;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import javax.servlet.MultipartConfigElement;


@SpringBootApplication
//引入配置文件
@ImportResource(locations = "classpath:spring.xml")

@MapperScan("com.huangchao.acef.dao") //扫描的mapper

public class AcefApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AcefApplication.class, args);
    }

    //最大图片大小
    @Value("${maxFileSize}")
    String maxFileSize;
    //最大请求大小
    @Value("${maxRequestSize}")
    String maxRequestSize;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize(maxFileSize); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(maxRequestSize);
        return factory.createMultipartConfig();
    }
}
