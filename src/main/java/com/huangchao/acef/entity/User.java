package com.huangchao.acef.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 本类为用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor//无参构造器
@AllArgsConstructor//全参构造器
public class User {
    private String id;
    private String emailAccount;    //账号（邮箱）
    private String password;

}
