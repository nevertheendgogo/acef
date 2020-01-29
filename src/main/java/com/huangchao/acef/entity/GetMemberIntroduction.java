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
public class GetMemberIntroduction {
    private String id;
    private String name;//中文名
    private String position;//中文职位名
    private String description;//中文简介
    private String imgPath;

}
