package com.huangchao.acef.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.UserMapper;
import com.huangchao.acef.entity.Feedback;
import com.huangchao.acef.entity.GetMemberIntroduction;
import com.huangchao.acef.entity.MemberIntroduction;
import com.huangchao.acef.entity.User;
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

    //保存用户反馈信息
    public void setFeedback(Feedback feedback) {
        mapper.setFeedback(feedback);
    }

    //意见反馈信息获取接口
    public PageInfo<Feedback> getFeedback(int currentPage, int pageSize) {
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<?> page = PageHelper.startPage(currentPage, pageSize);
        mapper.getFeedback();
        //用PageInfo对结果进行包装
        PageInfo<Feedback> pageInfo = (PageInfo<Feedback>) page.toPageInfo();
        return pageInfo;
    }

    //保存成员展示信息
    public void addShowMember(MemberIntroduction memberIntroduction) {
        mapper.addShowMember(memberIntroduction);
    }

    //注入本机公网ip地址
    @Value("${IpAddress}")
    String IpAddress;

    //获取所有成员信息
    public PageInfo<GetMemberIntroduction> getAllShowMemberIntroduction(String language, int currentPage, int pageSize) {
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<?> page = PageHelper.startPage(currentPage, pageSize);
        List<GetMemberIntroduction> list;
        if (language.equals("French")) {
            list = mapper.getAllFrenchShowMemberIntroduction();
        } else {
            list = mapper.getAllChineseShowMemberIntroduction();
        }

        //使图片路径完整
        if (list != null) {
            for (GetMemberIntroduction g : list) {
                g.setImgPath("http://" + IpAddress + g.getImgPath());
            }
        }

        //用PageInfo对结果进行包装
        PageInfo<GetMemberIntroduction> pageInfo = (PageInfo<GetMemberIntroduction>) page.toPageInfo();
        return pageInfo;
    }

    //后台管理展示成员信息获取
    public PageInfo<MemberIntroduction> getAllManageMemberIntroduction(int currentPage, int pageSize) {
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<?> page = PageHelper.startPage(currentPage, pageSize);
        List<MemberIntroduction> list = mapper.getAllManageMemberIntroduction();

        //使图片路径完整
        if (list != null) {
            for (MemberIntroduction s : list) {
                s.setImgPath("http://" + IpAddress + s.getImgPath());
            }
        }

        //用PageInfo对结果进行包装
        PageInfo<MemberIntroduction> pageInfo = (PageInfo<MemberIntroduction>) page.toPageInfo();
        return pageInfo;
    }

    //后台管理单个成员信息获取
    public MemberIntroduction getOneManageMemberIntroduction(int id) {
        MemberIntroduction memberIntroduction = mapper.getOneManageMemberIntroduction(id);
        if (memberIntroduction != null)
            memberIntroduction.setImgPath("http://" + IpAddress + memberIntroduction.getImgPath());
        return memberIntroduction;
    }

    //后台管理成员信息删除
    public void deleteMemberIntroduction(int[] idList) {
        mapper.deleteMemberIntroduction(idList);
    }

    //后台意见反馈信息批量删除
    public void deleteFeedback(int[] idList) {
        mapper.deleteFeedback(idList);
    }

    //后台管理成员信息修改
    public void changeMemberIntroduction(MemberIntroduction memberIntroduction) {
        mapper.changeMemberIntroduction(memberIntroduction);
    }


}
