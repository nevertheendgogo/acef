package com.huangchao.acef.dao;

import com.huangchao.acef.entity.OrdinaryArticle;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 本接口为普通文章持久层操作映射接口
 */

@Repository
public interface OrdinaryArticleRichTextMapper {

    //普通文章上传
    void uploadOrdinaryArticle(OrdinaryArticle oa);

    //根据普通文章id删除普通文章
    void deleteOrdinaryArticle(String[] articleId);

    //根据普通文章id获取普通文章
    OrdinaryArticle getOneOrdinaryArticle(String articleId);

    //批量获取普通文章
    List<OrdinaryArticle> getOrdinaryArticle(String language, String part);

    //判断articleId是否已经存在，即此普通文章是否已经上传
    String existOrdinaryArticleId(String articleId);

    //修改普通文章
    void changeOrdinaryArticle(OrdinaryArticle oa);
}
