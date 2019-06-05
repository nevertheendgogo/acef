package com.huangchao.acef.service;

import com.huangchao.acef.dao.FriendlyLinkMapper;
import com.huangchao.acef.entity.FriendlyLink;
import com.huangchao.acef.global.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 本类为友情链接处理类
 */

@Service
public class FriendlyLinkService {

    @Autowired
    FriendlyLinkMapper friendlyLinkMapper;
    //注入本机公网ip地址
    @Value("${IpAddress}")
    String IpAddress;
    //注入图片保存路径
    @Value("${filePath}")
    String filePath;
    //注入图片保存路径，相对根路径
    @Value("${imgPath}")
    String imgPath;
    //注入tomcat文件映射路径名
    @Value("${mapPath}")
    String mapPath;
    //注入友情链接图片保存路径
    @Value("${friendlyLinkPath}")
    String friendlyLinkPath;

    //上传友情链接
    public void uploadFriendlyLink(FriendlyLink friendlyLink, MultipartFile picture) throws IOException {

        //海报处理
        if (picture != null && !picture.isEmpty()) {
            //获取文件名
            String fileName = picture.getOriginalFilename();
            //获取文件后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //重新生成图片名
            fileName = UUID.randomUUID() + suffixName;
            //设置海报映射路径
            friendlyLink.setImgPath(mapPath + imgPath + friendlyLinkPath + fileName);
            //保存图片到指定文件夹,可能出现io异常
            picture.transferTo(new File(filePath + imgPath + friendlyLinkPath + fileName));
        }
        //将数据保存至数据库
        friendlyLinkMapper.uploadFriendlyLink(friendlyLink);
    }

    //删除友情链接
    public void deleteFriendlyLink(String[] id) throws IOException {
        String[] imgPaths=friendlyLinkMapper.getImgPaths(id);
        //删除数据库内容
        friendlyLinkMapper.deleteFriendlyLink(id);
        //删除图片
        for (String i:imgPaths) {
            Common.deletePreviousPicture(i,filePath,imgPath+friendlyLinkPath);
        }
    }



    //批量获取友情链接
    public List<FriendlyLink> getAllFriendlyLink() {
        List<FriendlyLink> friendlyLinks=friendlyLinkMapper.getAllFriendlyLink();
        //使图片路径完整
        for (FriendlyLink f:friendlyLinks) {
            f.setImgPath("http://"+IpAddress+f.getImgPath());
        }
        return friendlyLinks;
    }
}
