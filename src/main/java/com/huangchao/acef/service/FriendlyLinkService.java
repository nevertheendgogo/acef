package com.huangchao.acef.service;

import com.huangchao.acef.dao.FriendlyLinkMapper;
import com.huangchao.acef.entity.FriendlyLink;
import com.huangchao.acef.utils.Common;
import com.huangchao.acef.utils.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 本类为友情链接处理类
 */
@Service
public class FriendlyLinkService {

    @Autowired
    private FriendlyLinkMapper friendlyLinkMapper;
    //注入本机公网ip地址
    @Autowired
    private SystemConfig systemConfig;
    private final static Logger logger = LoggerFactory.getLogger(FriendlyLinkService.class);

    //上传友情链接
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadFriendlyLink(FriendlyLink friendlyLink, MultipartFile picture) throws IOException {
        Map<String, String> result = new HashMap<>();
        logger.info("上传的友情链接信息为：\n{}", friendlyLink);
        //海报处理
        if (picture != null && !picture.isEmpty()) {
            //获取文件名
            String fileName = picture.getOriginalFilename();
            //获取文件后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //重新生成图片名
            fileName = UUID.randomUUID() + suffixName;
            //设置海报映射路径
            friendlyLink.setImgPath(systemConfig.friendlyLinkPath + fileName);
            //保存图片到指定文件夹,可能出现io异常
            picture.transferTo(new File(systemConfig.filePath + systemConfig.friendlyLinkPath + fileName));
        }
        //将数据保存至数据库
        friendlyLinkMapper.uploadFriendlyLink(friendlyLink);
        result.put("result", "1");
        return result;
    }

    //删除友情链接
    @Transactional(rollbackFor = {Exception.class})//所有异常都回滚
    public Map<String, String> deleteFriendlyLink(String[] id) {
        logger.info("要删除的友情；链接id为：{}", Arrays.toString(id));
        Map<String, String> result = new HashMap<>();

        String[] imgPaths = friendlyLinkMapper.getImgPaths(id);
        //删除数据库内容
        friendlyLinkMapper.deleteFriendlyLink(id);
        //删除图片
        for (String i : imgPaths) {
            Common.deletePreviousPicture(i, systemConfig.filePath, systemConfig.friendlyLinkPath);
        }
        result.put("result", "1");
        return result;
    }

    //批量获取友情链接
    public List<FriendlyLink> getAllFriendlyLink() {
        List<FriendlyLink> friendlyLinks = friendlyLinkMapper.getAllFriendlyLink();
        //使图片路径完整
        for (FriendlyLink f : friendlyLinks) {
            f.setImgPath(systemConfig.urlPrefix + f.getImgPath());
        }
        return friendlyLinks;
    }
}
