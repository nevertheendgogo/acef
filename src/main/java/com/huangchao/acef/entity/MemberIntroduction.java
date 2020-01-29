package com.huangchao.acef.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 本类用于中文成员信息接受
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor//无参构造器
@AllArgsConstructor//全参构造器
public class MemberIntroduction {
    private String id;
    private String chName;//中文名
    private String chPos;//中文职位名
    private String chDes;//中文简介
    private String frName;//法文名
    private String frPos;//法文职位名
    private String frDes;//法文简介
    private int showPriority;//展示优先级
    private String imgPath;

}
