package com.huangchao.acef.controller;

import ch.qos.logback.classic.Logger;
import com.huangchao.acef.entity.*;
import com.huangchao.acef.service.DataService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.huangchao.acef.global.Common.getLanguage;

/**
 * 本类为跳转游客可访问的页面
 */
@Controller
@RequestMapping("/data")
public class DataOperation {

    //获取业务层操作类
    @Autowired
    private DataService dataService;
    private final static Logger logger = (Logger) LoggerFactory.getLogger(DataOperation.class);
    //注入默认界面语言
    @Value("${defaultLanguage}")
    String defaultLanguage;
    //注入本机公网ip地址
    @Value("${IpAddress}")
    String IpAddress;


    /***************************************************************  轮播图/协会介绍/富文本图片上传 *************************************************************************************************************/
    //轮播图或协会介绍图上传
    @RequestMapping("/up")
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadSlideshowOrAssociationIntroduction(MultipartFile[] slideshows, String part, Integer id, String url) {
        //用于返回结果
        Map<String, String> result = new HashMap<>();
        try {
            dataService.uploadSlideshowOrAssociationIntroduction(slideshows, part, id, url);
            result.put("result", "1");
        } catch (IOException e) {
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //轮播图/协会介绍链接获取
    @RequestMapping("/gp")
    @ResponseBody
    public List<Slideshow> getPicture(String part) {
        logger.info("logbackinfo 成功了");
        try {
            return dataService.getPicture(part);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //轮播图删除
    @RequestMapping("/dss")
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteSlideshow(int id, String url) {
        Map<String, String> result = new HashMap<>();

        try {
            //执行删除操作
            dataService.deleteSlideshow(id, url);
            result.put("result", "1");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //富文本(rice text)图片上传
    @RequestMapping("/urtp")
    @ResponseBody                                                           //文章id
    public Map<String, String> uploadRiceTextPicture(MultipartFile picture, String articleId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();

        String url;
        if (!picture.isEmpty() && articleId != null && !articleId.equals("")) {
            try {
                url = dataService.uploadRiceTextPicture(picture, articleId, request, response);
                result.put("articleId", articleId);
                result.put("url", url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    /***************************************************************  富文本文章 *************************************************************************************************************/

    //活动文章上传
    @RequestMapping("/uaa")
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        result.put("result", "0");

        //判断articleId是否已经存在，即此文章是否已经上传
        if (!dataService.existArticleId(aa.getArticleId())) {
            try {
                dataService.uploadActivityArticle(aa, activityTime, entryForm, poster, request, response);
                result.put("result", "1");
            } catch (IOException e) {
                e.printStackTrace();
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return result;
    }

    //根据文章id删除活动文章
    @RequestMapping("/daa")
    @ResponseBody
    public Map<String, String> deleteActivityArticle(String articleId) {
        Map<String, String> result = new HashMap<>();
        try {
            dataService.deleteActivityArticle(articleId);
            result.put("result", "1");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", "0");
        }
        return result;
    }

    //根据用户设置语言获取活动文章
    @RequestMapping("/gaa/{currentPage}/{pageSize}")
    @ResponseBody                               //当前页号                                                一页的数据量
    public List<ActivityArticle> getActivityArticle(@PathVariable(value = "currentPage") int currentPage, @PathVariable(value = "pageSize") int pageSize, HttpServletRequest request) {
        //获取用户设置的语言
        String language = getLanguage(request);
        return dataService.getActivityArticle(language != null ? language : defaultLanguage, currentPage, pageSize);
    }

}

