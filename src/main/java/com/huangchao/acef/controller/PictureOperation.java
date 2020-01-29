package com.huangchao.acef.controller;

import ch.qos.logback.classic.Logger;
import com.huangchao.acef.entity.Slideshow;
import com.huangchao.acef.service.PictureService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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


    //轮播图或协会介绍图上传（更新）
    @RequestMapping(value = "/ussoai", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadSlideshowOrAssociationIntroduction(MultipartFile[] slideshows, String part, Integer id, String url) throws IOException {
        return pictureService.uploadSlideshowOrAssociationIntroduction(slideshows, part, id, url);
    }

    /**
     * @param picture
     * @param articleId 文章id
     * @param request
     * @param response
     * @return
     */
    //富文本(rice text)图片上传
    @RequestMapping(value = "/urt", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadRiceTextPicture(MultipartFile picture, String articleId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return pictureService.uploadRiceTextPicture(picture, articleId, request, response);
    }

    //轮播图删除
    @RequestMapping(value = "/dss", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, String> deleteSlideshow(int id, String url) {
        return pictureService.deleteSlideshow(id, url);
    }

    //轮播图或协会介绍链接获取
    @RequestMapping(value = "/gssoai", method = RequestMethod.GET)
    @ResponseBody
    public List<Slideshow> getPicture(String part, HttpServletRequest request) {
        return pictureService.getPicture(part, request);
    }


}
