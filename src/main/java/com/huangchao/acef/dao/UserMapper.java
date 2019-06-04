package com.huangchao.acef.dao;

import com.huangchao.acef.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;

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


    //保存用户反馈信息
    void setFeedback(Feedback feedback);

    //意见反馈信息获取接口
    List<Feedback> getFeedback();

    //保存成员展示信息
    void addShowMember(MemberIntroduction memberIntroduction);

    //获取所有前端展示法语成员信息
    List<GetMemberIntroduction> getAllFrenchShowMemberIntroduction();
    //获取所有前端展示汉语成员信息
    List<GetMemberIntroduction> getAllChineseShowMemberIntroduction();

    //后台管理展示成员信息获取
    List<MemberIntroduction> getAllManageMemberIntroduction();

    //后台管理单个成员信息获取
    MemberIntroduction getOneManageMemberIntroduction(int id);

    //后台管理成员信息删除
    void deleteMemberIntroduction(int[] idList);

    //后台意见反馈信息批量删除
    void deleteFeedback(int[] idList);

    //后台管理成员信息修改
    void changeMemberIntroduction(MemberIntroduction memberIntroduction);
}
