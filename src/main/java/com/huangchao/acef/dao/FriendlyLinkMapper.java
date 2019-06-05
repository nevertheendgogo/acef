package com.huangchao.acef.dao;

import com.huangchao.acef.entity.FriendlyLink;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 本类为友情链接持久层映射类
 */

@Repository
public interface FriendlyLinkMapper {

    //友情链接上传
    void uploadFriendlyLink(FriendlyLink friendlyLink);

    //删除友情链接
    void deleteFriendlyLink(String[] id);

    //获取需要被删除的图图片链接
    String[] getImgPaths(String[] id);

    //批量获取友情链接
    List<FriendlyLink> getAllFriendlyLink();
}
