package com.huangchao.acef.dao;

import com.huangchao.acef.entity.Slideshow;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 本接口为图片处理持久层映射接口
 */

@Repository
public interface PictureMapper {

    //保存轮播图或协会介绍数据到数据库
    void uploadPicture(List<String> imgPaths, String part);

    //保存富文本上传的图片
    void addRichTextPicture(String articleId, String url);

    //删除轮播图
    void deleteSlideshow(int id);

    //删除中断富文本文章上传的图片
    void deleteRichTextPicture(String articleId);

    //查找富文本文章上传的图片
    String[] getRiceTextPictures(String articleId);

    //轮播图/协会介绍链接获取
    List<Slideshow> getPicture(String part);

    //    修改协会介绍
    void updateAssociationIntroduction(String url, String part, int id);
}
