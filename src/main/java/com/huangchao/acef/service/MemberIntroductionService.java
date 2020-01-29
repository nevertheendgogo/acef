package com.huangchao.acef.service;

import ch.qos.logback.classic.Logger;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.MemberIntroductionMapper;
import com.huangchao.acef.entity.GetMemberIntroduction;
import com.huangchao.acef.entity.MemberIntroduction;
import com.huangchao.acef.utils.Common;
import com.huangchao.acef.utils.SystemConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.huangchao.acef.utils.Common.deletePreviousPicture;
import static com.huangchao.acef.utils.Common.getLanguage;

/**
 * 本类为成员信息持久层映射类
 */
@Service
public class MemberIntroductionService {

    //注入映射用户信息相关数据库操作类
    @Autowired
    private MemberIntroductionMapper memberIntroductionMapper;

    @Autowired
    private SystemConfig systemConfig;

    private final static Logger logger = (Logger) LoggerFactory.getLogger(MemberIntroductionService.class);

    //保存成员展示信息
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, Object> memberIntroductionSave(MemberIntroduction memberIntroduction, MultipartFile picture) throws IOException {
        Map<String, Object> result = new HashMap<>();

        if (picture != null && !picture.isEmpty()) {
            //图片处理
            memberIntroduction = pictureDispose(memberIntroduction, picture);
        }
        memberIntroductionMapper.memberIntroductionSave(memberIntroduction);
        result.put("result", "1");
        return result;
    }

    //获取所有成员信息
    public PageInfo<GetMemberIntroduction> getAllShowMemberIntroduction(HttpServletRequest request, int currentPage, int pageSize) {
        String language = getLanguage(request);
        language = language != null ? language : systemConfig.defaultLanguage;
        logger.info("获取成员信息的当前语言为：{}", language);
        logger.info("当前分页号为：{}，每页大小为：{}", currentPage, pageSize);
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<GetMemberIntroduction> page = PageHelper.startPage(currentPage, pageSize);
        List<GetMemberIntroduction> list;
        if (language.equals("French")) {
            list = memberIntroductionMapper.getAllFrenchShowMemberIntroduction();
        } else {
            list = memberIntroductionMapper.getAllChineseShowMemberIntroduction();
        }

        //使图片路径完整
        if (list != null) {
            for (GetMemberIntroduction g : list) {
                g.setImgPath(systemConfig.urlPrefix + g.getImgPath());
            }
        }

        //用PageInfo对结果进行包装
        return page.toPageInfo();
    }

    //后台管理展示成员信息获取
    public PageInfo<MemberIntroduction> getAllManageMemberIntroduction(int currentPage, int pageSize) {
        logger.info("\n后台获取展示成员信息的当前分页号为：{}，每页大小为：{}", currentPage, pageSize);
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<MemberIntroduction> page = PageHelper.startPage(currentPage, pageSize);
        List<MemberIntroduction> list = memberIntroductionMapper.getAllManageMemberIntroduction();

        //使图片路径完整
        if (list != null) {
            for (MemberIntroduction s : list) {
                s.setImgPath(systemConfig.urlPrefix + s.getImgPath());
            }
        }

        //用PageInfo对结果进行包装
        return page.toPageInfo();
    }

    //后台管理单个成员信息获取
    public MemberIntroduction getOneManageMemberIntroduction(int id) {
        logger.info("后台管理单个成员信息获取的id为：{}", id);
        MemberIntroduction memberIntroduction = memberIntroductionMapper.getOneManageMemberIntroduction(id);
        if (memberIntroduction != null)
            memberIntroduction.setImgPath(systemConfig.urlPrefix + memberIntroduction.getImgPath());
        return memberIntroduction;
    }

    //后台管理成员信息删除
    public Map<String, String> deleteMemberIntroduction(int[] idList, String[] imgPaths) {
        logger.info("后台管理成员信息删除的id集合为：{}", Arrays.toString(idList));
        Map<String, String> result = new HashMap<>();

        if (imgPaths != null) {
            for (String imgUrl : imgPaths) {
                //删除图片
                deletePreviousPicture(imgUrl, systemConfig.filePath, systemConfig.memberIntroductionPath);
            }
        }
        memberIntroductionMapper.deleteMemberIntroduction(idList);
        if (imgPaths != null) {
            for (String s : imgPaths) {
                //删除图片
                Common.deletePreviousPicture(s, systemConfig.filePath, "");
            }
        }

        result.put("result", "1");
        return result;
    }

    //后台管理成员信息修改
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> changeMemberIntroduction(MemberIntroduction memberIntroduction, MultipartFile picture) throws IOException {
        logger.info("后台管理成员信息修改的信息如下：\n{}",memberIntroduction);
        Map<String, String> result = new HashMap<>();
//若重新上传了图片
        if (picture != null && !picture.isEmpty()) {
            //删除图片
            Common.deletePreviousPicture(memberIntroduction.getImgPath(), systemConfig.filePath, systemConfig.memberIntroductionPath);
            //图片处理
            memberIntroduction = pictureDispose(memberIntroduction, picture);

        } else {
            //设置图片映射路径为空，即不更新
            memberIntroduction.setImgPath(null);
        }
        memberIntroductionMapper.changeMemberIntroduction(memberIntroduction);
        result.put("result", "1");
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
        memberIntroduction.setImgPath(systemConfig.memberIntroductionPath + fileName);
        logger.info("要保存的文件全路径为:{}", systemConfig.filePath + systemConfig.memberIntroductionPath + fileName);
        //保存图片到指定文件夹,可能出现io异常
        picture.transferTo(new File(systemConfig.filePath + systemConfig.memberIntroductionPath + fileName));
        return memberIntroduction;
    }

}
