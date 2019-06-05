package com.huangchao.acef.dao;

import com.huangchao.acef.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 本接口为活动文章持久层操作映射接口
 */

@Repository
public interface ActivityArticleRichTextMapper {

    //活动文章保存
    void addActivityArticle(ActivityArticle aa);

    //根据文章id删除活动文章
    void deleteActivityArticle(String articleId);

    //根据用户设置语言和文章id获取活动文章
    ActivityArticle getOneActivityArticle(String articleId);

    //获取活动文章
    List<ActivityArticle> getActivityArticle(@Param("language")String language,@Param("part")String part);

    //判断articleId是否已经存在，即此活动文章是否已经上传
    String existArticleId(String articleId);

    //将修改后活动信息数据存进数据库
    void changeActivityArticle(ActivityArticle aa);

}
