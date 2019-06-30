package com.huangchao.acef.controller;

import com.github.pagehelper.PageInfo;
import com.huangchao.acef.entity.Feedback;
import com.huangchao.acef.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * 本类为意见反馈控制类
 */

@RestController
@RequestMapping("/fb")
public class FeedbackOperation {

    //获取业务层操作类
    @Autowired
    private FeedbackService feedbackService;

    //意见反馈上传接口
    @RequestMapping(value = "/u",method = RequestMethod.POST)
    public Map<String, String> uploadFeedback(Feedback feedback) {
        //用于保存保存结果
        Map<String, String> result = new HashMap<>();
        try {
            feedbackService.setFeedback(feedback);
            result.put("result", "1");

        } catch (Exception e) {
            result.put("result", "0");
            e.printStackTrace();
        }
        return result;
    }

    //后台意见反馈信息批量删除
    @RequestMapping(value = "/d",method = RequestMethod.DELETE)
    public Map<String, String> deleteFeedback(int[] idList) {
        //用于返回结果
        Map<String, String> result = new HashMap<>();
        try {
            feedbackService.deleteFeedback(idList);
            result.put("result", "1");
        } catch (Exception e) {
            result.put("result", "0");
            e.printStackTrace();
        }
        return result;
    }

    //意见反馈信息获取接口
    @RequestMapping(value = "/g",method = RequestMethod.GET)
    public PageInfo<Feedback> getFeedback(int currentPage, int pageSize) {
        return feedbackService.getFeedback(currentPage, pageSize);

    }

}

