package com.huangchao.acef.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.MemberIntroductionMapper;
import com.huangchao.acef.entity.GetMemberIntroduction;
import com.huangchao.acef.entity.MemberIntroduction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 本类为成员信息持久层映射类
 */
@Service
public class MemberIntroductionService {

    //注入映射用户信息相关数据库操作类
    @Autowired
    private MemberIntroductionMapper memberIntroductionMapper;

    //注入本机公网ip地址
    @Value("${IpAddress}")
    String IpAddress;

    //保存成员展示信息
    public void memberIntroductionSave(MemberIntroduction memberIntroduction) {
        memberIntroductionMapper.memberIntroductionSave(memberIntroduction);
    }

    //获取所有成员信息
    public PageInfo<GetMemberIntroduction> getAllShowMemberIntroduction(String language, int currentPage, int pageSize) {
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<?> page = PageHelper.startPage(currentPage, pageSize);
        List<GetMemberIntroduction> list;
        if (language.equals("French")) {
            list = memberIntroductionMapper.getAllFrenchShowMemberIntroduction();
        } else {
            list = memberIntroductionMapper.getAllChineseShowMemberIntroduction();
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
        List<MemberIntroduction> list = memberIntroductionMapper.getAllManageMemberIntroduction();

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
        MemberIntroduction memberIntroduction = memberIntroductionMapper.getOneManageMemberIntroduction(id);
        if (memberIntroduction != null)
            memberIntroduction.setImgPath("http://" + IpAddress + memberIntroduction.getImgPath());
        return memberIntroduction;
    }

    //后台管理成员信息删除
    public void deleteMemberIntroduction(int[] idList) {
        memberIntroductionMapper.deleteMemberIntroduction(idList);
    }

    //后台管理成员信息修改
    public void changeMemberIntroduction(MemberIntroduction memberIntroduction) {
        memberIntroductionMapper.changeMemberIntroduction(memberIntroduction);
    }

}
