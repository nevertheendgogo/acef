package com.huangchao.acef.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.FeedbackMapper;
import com.huangchao.acef.entity.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 本类为意见反馈处理类
 */
@Service
public class FeedbackService {

    //注入映射用户信息相关数据库操作类
    @Autowired
    private FeedbackMapper feedbackMapper;


    //保存用户反馈信息
    public void setFeedback(Feedback feedback) {
        feedbackMapper.setFeedback(feedback);
    }

    //意见反馈信息获取接口
    public PageInfo<Feedback> getFeedback(int currentPage, int pageSize) {
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<?> page = PageHelper.startPage(currentPage, pageSize);
        feedbackMapper.getFeedback();
        //用PageInfo对结果进行包装
        PageInfo<Feedback> pageInfo = (PageInfo<Feedback>) page.toPageInfo();
        return pageInfo;
    }

    //后台意见反馈信息批量删除
    public void deleteFeedback(int[] idList) {
        feedbackMapper.deleteFeedback(idList);
    }

}
