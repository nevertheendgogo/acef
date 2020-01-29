package com.huangchao.acef.entity;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor//无参构造器
@AllArgsConstructor//全参构造器
public class ActivityArticle {
    private String articleId;
    private String part;//文章所属专题
    private String language;
    private String title;
    private String author;
    private String displayTime;//发布时间：年月日
    private String activityStartTime;//活动开始时间：年月日
    private String activityEndTime;//活动结束时间：年月日
    private String content;//文章内容
    private String entryFormUrl;//报名表
    private String posterUrl;//海报

}

