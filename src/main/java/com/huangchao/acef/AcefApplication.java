package com.huangchao.acef;

import com.huangchao.acef.utils.SystemConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SystemConfig systemConfig;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize(systemConfig.maxFileSize); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(systemConfig.maxRequestSize);
        return factory.createMultipartConfig();
    }
}
