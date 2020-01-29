package com.huangchao.acef.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 将springboot配置文件得变量映射加载到本类，全局使用
 */
@Component//将对象的装配和声明周期管理交给spring容器
@ConfigurationProperties(prefix = "config")//执行映射的关键注解
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor//无参构造器
@AllArgsConstructor//全参构造器
public class SystemConfig {

    //ookie基准生存期（7天）
    public Integer survivalTime;
    //默认语言
    public String defaultLanguage;

    //请求所能携带的最大单个文件大小
    public String maxFileSize;
    //请求的最大大小
    public String maxRequestSize;

    //邮箱验证码法发送人邮箱
    public String fromEmail;
    //邮箱第三方登录授权码
    public String authorizationCode;
    //系统发生错误接收邮箱
    public String errorEmail;

    //活动文章图片/海报保存路径,相对图片保存路径
    public String activityArticleImgPath;
    //普通文章图片保存路径,相对图片保存路径
    public String articleImgPath;
    //报名表保存路径，相对根路径
    public String entryFormPath;
    //友情链接图片保存路径，相对图片保存路径
    public String friendlyLinkPath;
    //成员介绍图片存放路径，相对图片保存路径
    public String memberIntroductionPath;
    //轮播图或协会介绍图
    public String SlideshowOrAssociationIntroductionPath;
    //富文本图片路径
    public String richTextPath;
    //文件保存跟路径
    public String filePath;
    //服务器资源访问前缀
    public String urlPrefix;

}
