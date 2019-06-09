package com.huangchao.acef.service;

import com.huangchao.acef.dao.PictureMapper;
import com.huangchao.acef.entity.Slideshow;
import com.huangchao.acef.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.huangchao.acef.global.Common.deletePreviousPicture;

/**
 * 本类用于图片处理
 */

@Service
public class PictureService {

    //注入映射展示界面相关数据库操作类
    @Autowired
    private PictureMapper pictureMapper;
    //注入本机公网ip地址
    @Value("${IpAddress}")
    String IpAddress;
    //注入图片保存路径
    @Value("${filePath}")
    String filePath;
    //注入图片保存路径，相对根路径
    @Value("${imgPath}")
    String imgPath;
    //注入tomcat文件映射路径名
    @Value("${mapPath}")
    String mapPath;
    //注入活动文章图片或海报保存路径
    @Value("${activityArticleImgPath}")
    String activityArticleImgPath;
    //统一cookie存活基准时间
    @Value("${survivalTime}")
    int survivalTime;
    //轮播图或协会介绍图
    @Value("${SlideshowOrAssociationIntroductionPath}")
    String SlideshowOrAssociationIntroductionPath;

    //轮播图或协会介绍图上传
    public void uploadSlideshowOrAssociationIntroduction(MultipartFile[] slideshows, String part, Integer id, String url) throws IOException {
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
                imgPaths.add(mapPath + imgPath + fileName);
                //保存图片到指定文件夹,可能出现io异常
                slideshow.transferTo(new File(filePath + imgPath + SlideshowOrAssociationIntroductionPath + fileName));
            }
        }

        //若url不空则为更新协会介绍照片
        if (url != null && !url.equals("")) {
            //删除协会介绍图片
            deletePreviousPicture(url, filePath, imgPath + SlideshowOrAssociationIntroductionPath);
            //保存更新的协会介绍数据到数据库
            pictureMapper.updateAssociationIntroduction(imgPaths.get(0), part, id);
        } else
            //保存轮播图或协会介绍数据到数据库
            pictureMapper.uploadPicture(imgPaths, part);
    }

    //保存富文本上传的图片链接
    public void addRichTextPicture(String articleId, String url) {
        pictureMapper.addRichTextPicture(articleId, url);
    }

    //富文本(rice text)图片上传
    public String uploadRiceTextPicture(MultipartFile picture, String articleId, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String url;
        //获取文件名
        String fileName = picture.getOriginalFilename();
        //获取文件后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //重新生成图片名
        fileName = UUID.randomUUID() + suffixName;
        //设置图片映射路径
        url = mapPath + imgPath + activityArticleImgPath + fileName;
        //获取cookie
        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieUtil.findCookie(cookies, "articleId");

        //若id为articleId的cookie不为空且和当前文章id不同，则认为上次文章上传中断
        if (cookie != null && !cookie.getValue().equals(articleId)) {
            String[] riceTextPictures = pictureMapper.getRiceTextPictures(cookie.getValue() + "");
            for (int i = 0; i < riceTextPictures.length; i++) {
                //删除图片
                deletePreviousPicture(riceTextPictures[i], filePath, imgPath + activityArticleImgPath);
            }
            //删除数据库对应图片数据链接
            pictureMapper.deleteRichTextPicture(cookie.getValue());
        }
        CookieUtil.saveCookie("articleId", articleId, response, survivalTime * 3);

        //执行图片持久化操作
        addRichTextPicture(articleId, url);
        //保存图片到指定文件夹,可能出现io异常
        picture.transferTo(new File(filePath + imgPath + activityArticleImgPath + fileName));
        return "http://" + IpAddress + url;
    }

    //    轮播图删除
    public void deleteSlideshow(int id, String url) throws IOException {
        //删除数据库数据
        pictureMapper.deleteSlideshow(id);
        //删除图片
        deletePreviousPicture(url, filePath, imgPath);
    }

    //轮播图或协会介绍图片链接获取
    public List<Slideshow> getPicture(String part) {
        List<Slideshow> slideshowList = pictureMapper.getPicture(part);
        for (Slideshow s : slideshowList) {
            s.setUrl("http://" + IpAddress + s.getUrl());
        }
        return slideshowList;
    }

}
