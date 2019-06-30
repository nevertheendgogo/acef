package com.huangchao.acef.controller;

import ch.qos.logback.classic.Logger;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.entity.*;
import com.huangchao.acef.service.ActivityArticleRichTextService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * 本类为活动文章控制类
 */

@RestController
@RequestMapping("/aa")
public class ActivityArticleRichTextOperation {

    //获取业务层操作类
    @Autowired
    private ActivityArticleRichTextService activityArticleRichTextService;
    private final static Logger logger = (Logger) LoggerFactory.getLogger(ActivityArticleRichTextOperation.class);

    //注入默认界面语言
    @Value("${defaultLanguage}")
    String defaultLanguage;


    //活动文章上传
    @RequestMapping(value = "/u", method = RequestMethod.POST)
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        result.put("result", "0");

        //判断articleId是否已经存在，即此文章是否已经上传
        if (!activityArticleRichTextService.existArticleId(aa.getArticleId())) {
            try {
                activityArticleRichTextService.uploadActivityArticle(aa, activityTime, entryForm, poster, request, response);
                result.put("result", "1");
            } catch (IOException e) {
                logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n活动文章上传\n" + e);
                e.printStackTrace();
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return result;
    }

    //根据文章id删除活动文章
    @RequestMapping(value = "/d", method = RequestMethod.DELETE)
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteActivityArticle(String[] articleId) {
        Map<String, String> result = new HashMap<>();
        try {
            activityArticleRichTextService.deleteActivityArticle(articleId);
            result.put("result", "1");
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n根据文章id删除活动文章\n" + e);
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //根据文章id获取活动文章
    @GetMapping(value = "/go")
    public ActivityArticle getOneActivityArticle(String articleId) {
        //获取用户设置的语言
        try {
            return activityArticleRichTextService.getOneActivityArticle(articleId);
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n根据文章id获取活动文章\n" + e);
            e.printStackTrace();
            return null;
        }
    }

    //批量获取活动文章
    @RequestMapping(value = "/g", method = RequestMethod.GET)
                                                           //当前页号      一页的数据量     文章所属专题
    public PageInfo<ActivityArticle> getActivityArticle(int currentPage, int pageSize, String part, HttpServletRequest request, String language) {
        //若未传来language，，则为后台管理查询
        if (language == null || language.equals("")) {
            //获取用户设置的语言
            language = getLanguage(request);
        }
        try {
            return activityArticleRichTextService.getActivityArticle(language != null ? language : defaultLanguage, currentPage, pageSize, part);
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n根据用户设置语言批量获取活动文章\n" + e);
            e.printStackTrace();
            return null;
        }
    }

    //修改活动文章
    @RequestMapping(value = "/c", method = RequestMethod.PUT)
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> changeActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();

        try {
            activityArticleRichTextService.changeActivityArticle(aa, activityTime, entryForm, poster, request, response);
            result.put("result", "1");
        } catch (IOException e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n活动文章上传\n" + e);
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }


}

