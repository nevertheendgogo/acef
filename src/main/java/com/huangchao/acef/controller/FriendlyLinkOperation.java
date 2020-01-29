package com.huangchao.acef.controller;

import com.huangchao.acef.entity.FriendlyLink;
import com.huangchao.acef.service.FriendlyLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 本类为友情链接控制类
 */

@RestController
@RequestMapping("/fl")
public class FriendlyLinkOperation {

    //注入友情链接处理类
    @Autowired
    private FriendlyLinkService friendlyLinkService;

    //上传友情链接
    @RequestMapping(value = "/u", method = RequestMethod.POST)
    public Map<String, String> uploadFriendlyLink(FriendlyLink friendlyLink, MultipartFile picture) throws IOException {
        return friendlyLinkService.uploadFriendlyLink(friendlyLink, picture);
    }

    //删除友情链接
    @RequestMapping(value = "/d", method = RequestMethod.DELETE)
    public Map<String, String> deleteFriendlyLink(String[] id) throws IOException {
        return friendlyLinkService.deleteFriendlyLink(id);
    }

    //批量获取友情链接
    @RequestMapping(value = "/g", method = RequestMethod.GET)
    public List<FriendlyLink> getAllFriendlyLink() {
            return friendlyLinkService.getAllFriendlyLink();
    }
}
