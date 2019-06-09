package com.huangchao.acef.controller;

import ch.qos.logback.classic.Logger;
import com.huangchao.acef.entity.Slideshow;
import com.huangchao.acef.service.PictureService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本类用于图片处理控制
 */

@Controller
@RequestMapping("/img")
public class PictureOperation {

    //获取业务层操作类
    @Autowired
    private PictureService pictureService;
    private final static Logger logger = (Logger) LoggerFactory.getLogger(PictureOperation.class);
    //注入默认界面语言
    @Value("${defaultLanguage}")
    String defaultLanguage;

    //轮播图或协会介绍图上传（更新）
    @RequestMapping(value = "/ussoai", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadSlideshowOrAssociationIntroduction(MultipartFile[] slideshows, String part, Integer id, String url) {
        //用于返回结果
        Map<String, String> result = new HashMap<>();
        try {
            pictureService.uploadSlideshowOrAssociationIntroduction(slideshows, part, id, url);
            result.put("result", "1");
        } catch (IOException e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n轮播图或协会介绍图上传出错\n" + e);
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //富文本(rice text)图片上传
    @RequestMapping(value = "/urt", method = RequestMethod.POST)
    @ResponseBody                                                           //文章id
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadRiceTextPicture(MultipartFile picture, String articleId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        String url;
        if (picture != null && !picture.isEmpty() && articleId != null && !articleId.equals("")) {
            try {
                url = pictureService.uploadRiceTextPicture(picture, articleId, request, response);
                result.put("articleId", articleId);
                result.put("url", url);
            } catch (IOException e) {
                logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n富文本图片上传\n" + e);
                e.printStackTrace();
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return result;
    }

    //轮播图删除
    @RequestMapping(value = "/dss", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteSlideshow(int id, String url) {
        Map<String, String> result = new HashMap<>();

        try {
            //执行删除操作
            pictureService.deleteSlideshow(id, url);
            result.put("result", "1");
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n轮播图删除\n" + e);
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //轮播图或协会介绍链接获取
    @RequestMapping(value = "/gssoai", method = RequestMethod.GET)
    @ResponseBody
    public List<Slideshow> getPicture(String part) {
        try {
            return pictureService.getPicture(part);
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n轮播图或协会介绍链接获取\n" + e);
            e.printStackTrace();
        }
        return null;
    }


}
