package com.huangchao.acef.dao;

import com.huangchao.acef.entity.*;
import org.springframework.stereotype.Repository;


@Repository
public interface UserMapper {

    //查找用户信息findUserNameById
    User findUserByEmailAccount(String emailAccount);

    //查找用户id
    String findIdByEmailAccount(String emailAccount);

    //查找用户信息
    User findUserById(String id);

    //保存用户账号密码
    void  addUser(User user);

}
