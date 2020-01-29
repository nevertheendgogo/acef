package com.huangchao.acef.service;

import com.huangchao.acef.dao.PictureMapper;
import com.huangchao.acef.entity.Slideshow;
import com.huangchao.acef.utils.CookieUtil;
import com.huangchao.acef.utils.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.huangchao.acef.utils.Common.deletePreviousPicture;

/**
 * 本类用于图片处理
 */

@Service
public class PictureService {

    //注入映射展示界面相关数据库操作类
    @Autowired
    private PictureMapper pictureMapper;
    @Autowired
    private SystemConfig systemConfig;

    private final static Logger logger = LoggerFactory.getLogger(PictureService.class);

    //轮播图或协会介绍图上传
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadSlideshowOrAssociationIntroduction(MultipartFile[] slideshows, String part, Integer id, String url) throws IOException {
        logger.info("轮播图或协会介绍图上传的主题为：{},id为：{}", part, id);
        logger.info("图片地址为：{}", url);
        Map<String, String> result = new HashMap<>();

        //保存图片映射路径
        List<String> imgPaths = new ArrayList<>();
        for (MultipartFile slideshow : slideshows) {
            if (slideshow != null && !slideshow.isEmpty()) {
                //获取文件名
                String fileName = slideshow.getOriginalFilename();
                //获取文件后缀名
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                //重新生成图片名
                fileName = UUID.randomUUID() + suffixName;
                //设置图片映射路径
                imgPaths.add(systemConfig.SlideshowOrAssociationIntroductionPath + fileName);
                //保存图片到指定文件夹,可能出现io异常
                slideshow.transferTo(new File(systemConfig.filePath + systemConfig.SlideshowOrAssociationIntroductionPath + fileName));
            }
        }

        //若url不空则为更新协会介绍照片
        if (url != null && !url.equals("")) {
            //删除协会介绍图片
            deletePreviousPicture(url, systemConfig.filePath, systemConfig.SlideshowOrAssociationIntroductionPath);
            //保存更新的协会介绍数据到数据库
            pictureMapper.updateAssociationIntroduction(imgPaths.get(0), part, id);
        } else {
            //保存轮播图或协会介绍数据到数据库
            pictureMapper.uploadPicture(imgPaths, part);
        }

        result.put("result", "1");
        return result;
    }

    //保存富文本上传的图片链接
    public void addRichTextPicture(String articleId, String url) {
        logger.info("富文本图片上传的文章id为：{}\n图片地址为：{}", articleId, url);
        pictureMapper.addRichTextPicture(articleId, url);
    }

    //富文本(rice text)图片上传
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadRiceTextPicture(MultipartFile picture, String articleId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("富文本图片上传的文章id为：{}", articleId);
        Map<String, String> result = new HashMap<>();
        if (picture != null && !picture.isEmpty() && articleId != null && !articleId.equals("")) {
            //获取文件名
            String fileName = picture.getOriginalFilename();
            //获取文件后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //重新生成图片名
            fileName = UUID.randomUUID() + suffixName;
            //设置图片映射路径
            String url = systemConfig.richTextPath + fileName;
            //获取cookie
            Cookie[] cookies = request.getCookies();
            Cookie cookie = CookieUtil.findCookie(cookies, "articleId");

            //若id为articleId的cookie不为空且和当前文章id不同，则认为上次文章上传中断
            if (cookie != null && !cookie.getValue().equals(articleId)) {
                String[] riceTextPictures = pictureMapper.getRiceTextPictures(cookie.getValue() + "");
                for (String riceTextPicture : riceTextPictures) {
                    logger.info("上次上传中断，将删除原来的图片");
                    //删除图片
                    deletePreviousPicture(riceTextPicture, systemConfig.filePath, systemConfig.richTextPath);
                }
                //删除数据库对应图片数据链接
                pictureMapper.deleteRichTextPicture(cookie.getValue());
            }
            CookieUtil.saveCookie("articleId", articleId, response, systemConfig.survivalTime * 3);

            //执行图片持久化操作
            addRichTextPicture(articleId, url);
            logger.info("富文本图片保存全路径为：{}", systemConfig.filePath + systemConfig.richTextPath + fileName);
            //保存图片到指定文件夹,可能出现io异常
            picture.transferTo(new File(systemConfig.filePath + systemConfig.richTextPath + fileName));
            result.put("articleId", articleId);
            result.put("url", systemConfig.urlPrefix + url);
        }
        return result;
    }

    //    轮播图删除
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteSlideshow(int id, String url) {
        logger.info("要删除轮播图的id为：{}\n图片地址为：{}", id, url);
        Map<String, String> result = new HashMap<>();

        //删除数据库数据
        pictureMapper.deleteSlideshow(id);
        //删除图片
        deletePreviousPicture(url, systemConfig.filePath, systemConfig.SlideshowOrAssociationIntroductionPath);
        result.put("result", "1");
        return result;
    }

    //轮播图或协会介绍图片链接获取
    public List<Slideshow> getPicture(String part, HttpServletRequest request) {
        logger.info("轮播图或协会介绍图片链接获取的主题为：{}", part);
        List<Slideshow> slideshowList = pictureMapper.getPicture(part);
        for (Slideshow s : slideshowList) {
            s.setUrl(systemConfig.urlPrefix + s.getUrl());
        }
        return slideshowList;
    }

}
