package com.huangchao.acef.dao;

import com.huangchao.acef.entity.GetMemberIntroduction;
import com.huangchao.acef.entity.MemberIntroduction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberIntroductionMapper {

    //保存成员展示信息
    void memberIntroductionSave(MemberIntroduction memberIntroduction);

    //后台管理成员信息删除
    void deleteMemberIntroduction(int[] idList);

    //获取所有前端展示法语成员信息
    List<GetMemberIntroduction> getAllFrenchShowMemberIntroduction();

    //获取所有前端展示汉语成员信息
    List<GetMemberIntroduction> getAllChineseShowMemberIntroduction();

    //后台管理展示成员信息获取
    List<MemberIntroduction> getAllManageMemberIntroduction();

    //后台管理单个成员信息获取
    MemberIntroduction getOneManageMemberIntroduction(int id);

    //后台管理成员信息修改
    void changeMemberIntroduction(MemberIntroduction memberIntroduction);
}
