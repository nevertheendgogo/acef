package com.huangchao.acef.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 本类位普通文章实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor//无参构造器
@AllArgsConstructor//全参构造器
public class OrdinaryArticle {
    private String articleId;
    private String part;//所属专题
    private String language;
    private String title;
    private String author;
    private String displayTime;//发布时间
    private String content;//文章内容

}

