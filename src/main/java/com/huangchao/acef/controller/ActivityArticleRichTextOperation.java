package com.huangchao.acef.controller;

import com.github.pagehelper.PageInfo;
import com.huangchao.acef.entity.ActivityArticle;
import com.huangchao.acef.service.ActivityArticleRichTextService;
import com.huangchao.acef.utils.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.huangchao.acef.utils.Common.getLanguage;

/**
 * 本类为活动文章控制类
 */

@RestController
@RequestMapping("/aa")
public class ActivityArticleRichTextOperation {

    //获取业务层操作类
    @Autowired
    private ActivityArticleRichTextService activityArticleRichTextService;

    @Autowired
    private SystemConfig systemConfig;

    //活动文章上传
    @RequestMapping(value = "/u", method = RequestMethod.POST)
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //判断articleId是否已经存在，即此文章是否已经上传
        if (!activityArticleRichTextService.existArticleId(aa.getArticleId())) {
            return activityArticleRichTextService.uploadActivityArticle(aa, activityTime, entryForm, poster, request, response);
        }
        return null;
    }

    //根据文章id删除活动文章
    @RequestMapping(value = "/d", method = RequestMethod.DELETE)
    public Map<String, String> deleteActivityArticle(String[] articleId) throws IOException {
        return activityArticleRichTextService.deleteActivityArticle(articleId);
    }

    //根据文章id获取活动文章
    @GetMapping(value = "/go")
    public ActivityArticle getOneActivityArticle(String articleId) {
        //获取用户设置的语言
        return activityArticleRichTextService.getOneActivityArticle(articleId);
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
        return activityArticleRichTextService.getActivityArticle(language != null ? language : systemConfig.defaultLanguage, currentPage, pageSize, part);
    }

    //修改活动文章
    @RequestMapping(value = "/c", method = RequestMethod.PUT)
    public Map<String, String> changeActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return activityArticleRichTextService.changeActivityArticle(aa, activityTime, entryForm, poster, request, response);
    }


}

