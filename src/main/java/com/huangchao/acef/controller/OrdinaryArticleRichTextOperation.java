package com.huangchao.acef.controller;

import ch.qos.logback.classic.Logger;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.entity.OrdinaryArticle;
import com.huangchao.acef.service.OrdinaryArticleRichTextService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.huangchao.acef.global.Common.getLanguage;

/**
 * 本类为普通文章控制类
 */
@Controller
@RequestMapping("/oa")
public class OrdinaryArticleRichTextOperation {

    //获取业务层操作类
    @Autowired
    private OrdinaryArticleRichTextService ordinaryArticleRichTextService;
    private final static Logger logger = (Logger) LoggerFactory.getLogger(OrdinaryArticleRichTextOperation.class);
    //注入默认界面语言
    @Value("${defaultLanguage}")
    String defaultLanguage;


    //普通文章上传
    @RequestMapping(value = "/u", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadOrdinaryArticle(OrdinaryArticle oa, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        //判断articleId是否已经存在，即此文章是否已经上传
        if (!ordinaryArticleRichTextService.existOrdinaryArticleId(oa.getArticleId())) {
            try {
                ordinaryArticleRichTextService.uploadOrdinaryArticle(oa, request, response);
                result.put("result", "1");
            } catch (Exception e) {
                logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n活动文章上传\n" + e);
                e.printStackTrace();
                result.put("result", "0");
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return result;
    }

    //根据普通文章id删除普通文章
    @RequestMapping(value = "/d", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteOrdinaryArticle(String[] articleId) {
        Map<String, String> result = new HashMap<>();
        try {
            ordinaryArticleRichTextService.deleteOrdinaryArticle(articleId);
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

    //根据普通文章id获取普通文章
    @GetMapping(value = "/go")
    @ResponseBody
    public OrdinaryArticle getOneOrdinaryArticle(String articleId) {
        //获取用户设置的语言
        try {
            return ordinaryArticleRichTextService.getOneOrdinaryArticle(articleId);
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n根据文章id获取活动文章\n" + e);
            e.printStackTrace();
            return null;
        }
    }

    //批量获取普通文章
    @RequestMapping(value = "/g", method = RequestMethod.GET)
    @ResponseBody                                       //当前页号      一页的数据量     文章所属专题
    public PageInfo<OrdinaryArticle> getOrdinaryArticle(int currentPage, int pageSize, String part, HttpServletRequest request, String language) {
        //若未传来language，则为后台管理查询
        if (language == null || language.equals("")) {
            //获取用户设置的语言
            language = getLanguage(request);
        }
        try {
            return ordinaryArticleRichTextService.getOrdinaryArticle(language != null ? language : defaultLanguage, currentPage, pageSize, part);
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n根据用户设置语言批量获取活动文章\n" + e);
            e.printStackTrace();
            return null;
        }
    }

    //修改普通文章
    @RequestMapping(value = "/c", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> changeOrdinaryArticle(OrdinaryArticle oa) {
        Map<String, String> result = new HashMap<>();

        try {
            ordinaryArticleRichTextService.changeOrdinaryArticle(oa);
            result.put("result", "1");
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n活动文章上传\n" + e);
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }


}

