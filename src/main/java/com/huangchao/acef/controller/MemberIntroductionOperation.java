package com.huangchao.acef.controller;

import com.github.pagehelper.PageInfo;
import com.huangchao.acef.entity.GetMemberIntroduction;
import com.huangchao.acef.entity.MemberIntroduction;
import com.huangchao.acef.global.Common;
import com.huangchao.acef.service.MemberIntroductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.huangchao.acef.global.Common.getLanguage;


/**
 * 本类为成员信息控制类
 */

@Controller
@RequestMapping("/mi")
public class MemberIntroductionOperation {

    //获取业务层操作类
    @Autowired
    private MemberIntroductionService memberIntroductionService;
    //统一cookie存活基准时间
    @Value("${survivalTime}")
    private int survivalTime;
    //注入默认界面语言
    @Value("${defaultLanguage}")
    String defaultLanguage;
    //注入图片保存路径
    @Value("${filePath}")
    String filePath;
    //注入图片保存路径，相对根路径
    @Value("${imgPath}")
    String imgPath;
    //注入tomcat文件映射路径名
    @Value("${mapPath}")
    String mapPath;

    //成员信息上传
    @RequestMapping(value = "/u", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, Object> memberIntroductionSave(MemberIntroduction memberIntroduction, MultipartFile picture) {
        //用于返回保存结果
        Map<String, Object> result = new HashMap<>();

        try {
            if (picture != null && !picture.isEmpty()) {
                //图片处理
                memberIntroduction = pictureDispose(memberIntroduction, picture);
            }
            //保存数据到数据库，可能出现异常
            memberIntroductionService.memberIntroductionSave(memberIntroduction);
            result.put("result", "1");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return result;
        }

    }

    //后台管理成员信息删除
    @RequestMapping(value = "/d",method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteMemberIntroduction(int[] idList, String[] imgPaths) {
        //用于返回结果
        Map<String, String> result = new HashMap<>();

        try {
            //执行删除操作
            memberIntroductionService.deleteMemberIntroduction(idList);
            if (imgPaths != null) {
                for (String s : imgPaths) {
                    //删除图片
                    Common.deletePreviousPicture(s, filePath, imgPath);
                }
            }

            result.put("result", "1");
        } catch (Exception e) {
            result.put("result", "0");
            e.printStackTrace();
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //前端展示成员信息获取
    @RequestMapping(value = "/gaf",method = RequestMethod.GET)
    @ResponseBody                                                            //当前页号  一页的数据量
    public PageInfo<GetMemberIntroduction> getAllShowMemberIntroduction(int currentPage, int pageSize, HttpServletRequest request) {
        String language = getLanguage(request);
        return memberIntroductionService.getAllShowMemberIntroduction(language != null ? language : defaultLanguage, currentPage, pageSize);
    }

    //后台管理展示成员信息获取
    @RequestMapping(value = "/gab",method = RequestMethod.GET)
    @ResponseBody                                                           //当前页号   一页的数据量
    public PageInfo<MemberIntroduction> getAllManageMemberIntroduction(int currentPage, int pageSize) {
        return memberIntroductionService.getAllManageMemberIntroduction(currentPage, pageSize);
    }

    //后台管理单个成员信息获取
    @RequestMapping(value = "/gob",method = RequestMethod.GET)
    @ResponseBody
    public MemberIntroduction getOneManageMemberIntroduction(int id) {
        return memberIntroductionService.getOneManageMemberIntroduction(id);
    }

    //后台管理成员信息修改
    @RequestMapping(value = "/c",method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> changeMemberIntroduction(MemberIntroduction memberIntroduction, MultipartFile picture) {
        //用于返回结果
        Map<String, String> result = new HashMap<>();
        try {
            //若重新上传了图片
            if (picture != null && !picture.isEmpty()) {
                //删除图片
                Common.deletePreviousPicture(memberIntroduction.getImgPath(), filePath, imgPath);
                //图片处理
                memberIntroduction = pictureDispose(memberIntroduction, picture);

            } else {
                //设置图片映射路径为空，即不更新
                memberIntroduction.setImgPath(null);
            }
            //执行修改操作
            memberIntroductionService.changeMemberIntroduction(memberIntroduction);
            result.put("result", "1");

        } catch (Exception e) {
            result.put("result", "0");
            e.printStackTrace();
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //辅助方法、图片保存
    private MemberIntroduction pictureDispose(MemberIntroduction memberIntroduction, MultipartFile picture) throws IOException {
        //获取文件名
        String fileName = picture.getOriginalFilename();
        //获取文件后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //重新生成图片名
        fileName = UUID.randomUUID() + suffixName;
        //设置图片映射路径
        memberIntroduction.setImgPath(mapPath + imgPath + fileName);
        //保存图片到指定文件夹,可能出现io异常
        picture.transferTo(new File(filePath + imgPath + fileName));
        return memberIntroduction;
    }


}

