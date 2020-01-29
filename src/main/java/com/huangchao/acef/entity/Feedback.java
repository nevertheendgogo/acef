package com.huangchao.acef.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 此类位意见反馈实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor//无参构造器
@AllArgsConstructor//全参构造器
public class Feedback {

    private int id;
    private String emailAccount;//邮箱
    private String userName;
    private String phone;
    private String title;
    private String description;
    private String createTime;

}
