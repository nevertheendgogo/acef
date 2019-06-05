package com.huangchao.acef.controller;

import ch.qos.logback.classic.Logger;
import com.huangchao.acef.entity.FriendlyLink;
import com.huangchao.acef.service.FriendlyLinkService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本类为友情链接控制类
 */

@Controller
@RequestMapping("/fl")
public class FriendlyLinkOperation {

    //注入友情链接处理类
    @Autowired
    private FriendlyLinkService friendlyLinkService;

    private final static Logger logger = (Logger) LoggerFactory.getLogger(ActivityArticleRichTextOperation.class);

    //上传友情链接
    @RequestMapping(value = "/u",method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String,String> uploadFriendlyLink(FriendlyLink friendlyLink, MultipartFile picture){
        Map<String,String> result=new HashMap<>();

        try {
            friendlyLinkService.uploadFriendlyLink(friendlyLink,picture);
            result.put("result","1");
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n上传友情链接\n" + e);
            e.printStackTrace();
            result.put("result","0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //删除友情链接
    @RequestMapping(value = "/d",method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class})//所有异常都回滚
    public Map<String,String> deleteFriendlyLink(String[] id){
        Map<String,String> result=new HashMap<>();

        try {
            friendlyLinkService.deleteFriendlyLink(id);
            result.put("result","1");
        } catch (Exception e) {
            logger.error("\n\n*****************************************************************************************************************************************************************************" + "\n\n删除友情链接\n" + e);
            e.printStackTrace();
            result.put("result","0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //批量获取友情链接
    @RequestMapping(value = "/g",method = RequestMethod.GET)
    @ResponseBody
    public List<FriendlyLink> getAllFriendlyLink(){
        try {
            return friendlyLinkService.getAllFriendlyLink();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
