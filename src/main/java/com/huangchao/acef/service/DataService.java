package com.huangchao.acef.service;

import com.huangchao.acef.dao.DataMapper;
import com.huangchao.acef.entity.ActivityArticle;
import com.huangchao.acef.entity.FormData;
import com.huangchao.acef.entity.Slideshow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 本类为用户数据相关操作
 */
@Service
public class DataService {

    //注入映射展示界面相关数据库操作类
    @Autowired
    private DataMapper mapper;
    //注入本机公网ip地址
    @Value("${IpAddress}")
    String IpAddress;


    //保存页面展示数据
    public void addData(FormData formData) {
        mapper.addData(formData);
    }

    //保存轮播图或协会介绍数据到数据库
    public void uploadPicture(List<String> imgPaths, String part) {
        mapper.uploadPicture(imgPaths, part);
    }

    //修改协会介绍
    public void updateAssociationIntroduction(String url, String part, int id) {
        mapper.updateAssociationIntroduction(url, part, id);
    }

    //轮播图/协会介绍链接获取
    public List<Slideshow> getPicture(String part) {
        List<Slideshow> slideshowList = mapper.getPicture(part);
        for (Slideshow s : slideshowList) {
            s.setUrl("http://" + IpAddress + s.getUrl());
        }
        return slideshowList;
    }

    //    轮播图删除
    public void deleteSlideshow(int id) {
        mapper.deleteSlideshow(id);
    }

    //保存富文本上传的图片
    public void addRichTextPicture(String articleId, String url) {
        mapper.addRichTextPicture(articleId, url);
    }

    //保存富文本上传的海报
    public void addRichTextPoster(String articleId, String posterUrl) {
        mapper.addRichTextPoster(articleId, posterUrl);
    }

    //活动文章保存
    public void uploadActivityArticle(ActivityArticle aa) {
        mapper.uploadActivityArticle(aa);
    }

    //删除中断富文本文章上传的图片
    public void deleteRichTextPicture(String articleId) {
        mapper.deleteRichTextPicture(articleId);
    }

    //查找富文本文章上传的图片
    public String[] getRiceTextPictures(String articleId) {
        return mapper.getRiceTextPictures(articleId);
    }

    //    判断articleId是否已经存在，即此文章是否已经上传
    public boolean existArticleId(String articleId) {
        return mapper.existArticleId(articleId)!=null;
    }

    //获取活动文章
    public List<ActivityArticle> getActivityArticle(String language, int currentPage, int pageSize) {
        return mapper.getActivityArticle(language,currentPage,pageSize);
    }



//    //查找页面展示数据（法语）
//    public GetData findFrenchDataByPart(String part) {
//        return mapper.findFrenchDataByPart(part);
//    }
//
//    //查找页面展示数据（英语）
//    public GetData findEnglishDataByPart(String part) {
//        return mapper.findEnglishDataByPart(part);
//    }
//
//    //查找页面展示数据（中文）
//    public GetData findChineseDataByPart(String part) {
//        return mapper.findChineseDataByPart(part);
//    }


}
