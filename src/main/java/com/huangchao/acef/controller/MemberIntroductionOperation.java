package com.huangchao.acef.controller;

import com.github.pagehelper.PageInfo;
import com.huangchao.acef.entity.GetMemberIntroduction;
import com.huangchao.acef.entity.MemberIntroduction;
import com.huangchao.acef.service.MemberIntroductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;


/**
 * 本类为成员信息控制类
 */

@RestController
@RequestMapping("/mi")
public class MemberIntroductionOperation {

    //获取业务层操作类
    @Autowired
    private MemberIntroductionService memberIntroductionService;


    //成员信息上传
    @RequestMapping(value = "/u", method = RequestMethod.POST)
    public Map<String, Object> uploadMemberIntroduction(MemberIntroduction memberIntroduction, MultipartFile picture) throws IOException {
        return memberIntroductionService.memberIntroductionSave(memberIntroduction, picture);
    }

    //后台管理成员信息删除
    @RequestMapping(value = "/d", method = RequestMethod.DELETE)
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteMemberIntroduction(int[] idList, @RequestParam("imgPaths") String[] imgPaths) throws IOException {
        return memberIntroductionService.deleteMemberIntroduction(idList, imgPaths);
    }

    //前端展示成员信息获取
    @RequestMapping(value = "/gaf", method = RequestMethod.GET)
    //当前页号  一页的数据量
    public PageInfo<GetMemberIntroduction> getAllShowMemberIntroduction(int currentPage, int pageSize, HttpServletRequest request) {
        return memberIntroductionService.getAllShowMemberIntroduction(request, currentPage, pageSize);
    }

    /**
     * @param currentPage 当前页号
     * @param pageSize    一页的数据量
     * @return
     */
    //后台管理展示成员信息获取
    @RequestMapping(value = "/gab", method = RequestMethod.GET)
    public PageInfo<MemberIntroduction> getAllManageMemberIntroduction(int currentPage, int pageSize) {
        return memberIntroductionService.getAllManageMemberIntroduction(currentPage, pageSize);
    }

    //后台管理单个成员信息获取
    @RequestMapping(value = "/gob", method = RequestMethod.GET)
    public MemberIntroduction getOneManageMemberIntroduction(int id) {
        return memberIntroductionService.getOneManageMemberIntroduction(id);
    }

    //后台管理成员信息修改
    @RequestMapping(value = "/c", method = RequestMethod.PUT)
    public Map<String, String> changeMemberIntroduction(MemberIntroduction memberIntroduction, MultipartFile picture) throws IOException {
        return memberIntroductionService.changeMemberIntroduction(memberIntroduction, picture);
    }
}
