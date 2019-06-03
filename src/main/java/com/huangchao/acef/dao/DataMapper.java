package com.huangchao.acef.dao;

import com.huangchao.acef.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataMapper {
    //查找用户信息findUserNameById
    User findUserByEmailAccount(String emailAccount);

    //查找用户id
    String findIdByEmailAccount(String emailAccount);

    //查找用户信息
    User findUserById(String id);

    //保存用户账号密码
    void addUser(User user);

    //保存页面展示数据
    void addData(FormData formData);

    //保存用户反馈信息
    void setFeedback(Feedback feedback);

    //查找页面展示数据（法语）
//    GetData findFrenchDataByPart(String part);
//    //查找页面展示数据英语（）
//    GetData  findEnglishDataByPart(String part);
//    //查找页面展示数据（中文）
//    GetData findChineseDataByPart(String part);
    //意见反馈信息获取接口
    List<Feedback> getFeedback();

    //保存轮播图或协会介绍数据到数据库
    void uploadPicture(List<String> imgPaths, String part);

    //    修改协会介绍
    void updateAssociationIntroduction(String url, String part, int id);

    //轮播图/协会介绍链接获取
    List<Slideshow> getPicture(String part);

    //删除轮播图
    void deleteSlideshow(int id);

    //保存富文本上传的图片
    void addRichTextPicture(String articleId, String url);

    //保存富文本上传的海报
    void addRichTextPoster(String articleId, String posterUrl);

    //活动文章保存
    void uploadActivityArticle(ActivityArticle aa);

    //删除中断富文本文章上传的图片
    void deleteRichTextPicture(String articleId);

    //查找富文本文章上传的图片
    String[] getRiceTextPictures(String articleId);

    //    判断articleId是否已经存在，即此文章是否已经上传
    String existArticleId(String articleId);

    //获取活动文章
    List<ActivityArticle> getActivityArticle(String language, int currentPage, int pageSize);
}
