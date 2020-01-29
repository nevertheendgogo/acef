package com.huangchao.acef.controller;

import ch.qos.logback.classic.Logger;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.entity.OrdinaryArticle;
import com.huangchao.acef.service.OrdinaryArticleRichTextService;
import com.huangchao.acef.utils.SystemConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 本类为普通文章控制类
 */
@Controller
@RequestMapping("/oa")
public class OrdinaryArticleRichTextOperation {

    //获取业务层操作类
    @Autowired
    private OrdinaryArticleRichTextService ordinaryArticleRichTextService;

    //普通文章上传
    @RequestMapping(value = "/u", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadOrdinaryArticle(OrdinaryArticle oa, HttpServletRequest request, HttpServletResponse response) {
        return ordinaryArticleRichTextService.uploadOrdinaryArticle(oa, request, response);
    }

    //根据普通文章id删除普通文章
    @RequestMapping(value = "/d", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, String> deleteOrdinaryArticle(String[] articleId) {
        return ordinaryArticleRichTextService.deleteOrdinaryArticle(articleId);
    }

    //根据普通文章id获取普通文章
    @GetMapping(value = "/go")
    @ResponseBody
    public OrdinaryArticle getOneOrdinaryArticle(String articleId) {
        return ordinaryArticleRichTextService.getOneOrdinaryArticle(articleId);
    }

    /**
     * @param currentPage 当前页号
     * @param pageSize    一页的数据量
     * @param part        文章所属专题
     * @param request
     * @param language    语言
     * @return
     */
    //批量获取普通文章
    @RequestMapping(value = "/g", method = RequestMethod.GET)
    @ResponseBody
    public PageInfo<OrdinaryArticle> getOrdinaryArticle(int currentPage, int pageSize, String part, HttpServletRequest request, String language) {
        return ordinaryArticleRichTextService.getOrdinaryArticle(language, currentPage, pageSize, part, request);
    }

    //修改普通文章
    @RequestMapping(value = "/c", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, String> changeOrdinaryArticle(OrdinaryArticle oa) {
        return ordinaryArticleRichTextService.changeOrdinaryArticle(oa);
    }


}

