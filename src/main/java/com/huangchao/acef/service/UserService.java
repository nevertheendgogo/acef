package com.huangchao.acef.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.UserMapper;
import com.huangchao.acef.entity.Feedback;
import com.huangchao.acef.entity.GetMemberIntroduction;
import com.huangchao.acef.entity.MemberIntroduction;
import com.huangchao.acef.entity.User;
import com.huangchao.acef.utils.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 本类为用户数据相关操作
 */
@Service
public class UserService {

    //注入映射用户信息相关数据库操作类
    @Autowired
    private UserMapper mapper;

    //查找用户信息
    public User findUserByEmailAccount(String emailAccount) {
        return mapper.findUserByEmailAccount(emailAccount);
    }

    //查找用户id
    public String findIdByEmailAccount(String emailAccount) {
        return mapper.findIdByEmailAccount(emailAccount);
    }

    //查找用户信息
    public User findUserById(String id) {
        return mapper.findUserById(id);
    }

    //保存用户账号密码
    public void addUser(User user) {
        mapper.addUser(user);
    }

    //修改密码
    public int changePassword(String emailAccount, String newPassword) {
        return mapper.changePassword(emailAccount, Md5.encode(newPassword));
    }
}
